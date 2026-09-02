package com.berkaykomur.backend.scrapper.impl;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ScrapperResult;
import com.berkaykomur.backend.exception.InvalidProductUrlException;
import com.berkaykomur.backend.exception.JsonLdNotFoundException;
import com.berkaykomur.backend.exception.ProductParsingException;
import com.berkaykomur.backend.exception.ScrapingConnectionException;
import com.berkaykomur.backend.scrapper.Scrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class TrendyolScrapper implements Scrapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_COMMENT_PAGES = 20;

    @Override
    public ScrapperResult scrap(String productUrl) {
        Document document;
        try {
            document = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new ScrapingConnectionException("İlgili siteye bağlanılamadı: " + productUrl, e);
        }

        Elements jsonLdScripts = document.select("script[type=application/ld+json]");
        if (jsonLdScripts.isEmpty()) {
            throw new JsonLdNotFoundException("Sayfada hiçbir application/ld+json etiketi bulunamadı");
        }

        JsonNode productNode = null;
        JsonNode webPageNode = null;

        for (Element script : jsonLdScripts) {
            try {
                JsonNode node = objectMapper.readTree(script.data());

                if (node.isArray() && !node.isEmpty()) {
                    node = node.get(0);
                }

                if (node.has("@type")) {
                    String type = node.get("@type").asText();
                    if ("ProductGroup".equalsIgnoreCase(type) || "Product".equalsIgnoreCase(type)) {

                        if (productNode == null || node.hasNonNull("aggregateRating")) {
                            productNode = node;
                        }
                    } else if ("WebPage".equalsIgnoreCase(type) && node.has("breadcrumb")) {
                        webPageNode = node;
                    }
                }
            } catch (Exception ignored) {
                // İlgisiz veya hatalı formattaki scriptleri sessizce atlıyoruz
            }
        }

        if (productNode == null) {
            throw new ProductParsingException("Sayfada @type='Product' olan JSON-LD etiketi bulunamadı.");
        }

        String name = require(productNode, "name").asText();
        JsonNode offers = require(productNode, "offers");
        BigDecimal price = new BigDecimal(require(offers, "price").asText());

        JsonNode image = require(productNode, "image");
        JsonNode contentUrls = require(image, "contentUrl");
        if (!contentUrls.isArray() || contentUrls.isEmpty()) {
            throw new ProductParsingException("Ürün resmi çekilemedi: " + contentUrls);
        }
        String imageUrl = contentUrls.get(0).asText();

        int ratingCount = 0;
        int reviewCount = 0;
        double rating = 0.0;
        if (productNode.hasNonNull("aggregateRating")) {
            JsonNode aggregateRating = productNode.get("aggregateRating");
            ratingCount = aggregateRating.path("ratingCount").asInt(0);
            reviewCount = aggregateRating.path("reviewCount").asInt(0);
            rating = aggregateRating.path("ratingValue").asDouble(0.0);
        }

        String categoryPath = "Diğer";

        if (webPageNode != null) {
            JsonNode itemList = webPageNode.path("breadcrumb").path("itemListElement");
            if (itemList.isArray() && !itemList.isEmpty()) {

                JsonNode targetNode = itemList.get(3);
                String categoryName = targetNode.path("item").path("name").asText("");
                if (!categoryName.isEmpty()) {
                    categoryPath = categoryName;
                }
            }
        }

        return ScrapperResult.builder()
                .name(name)
                .productUrl(productUrl)
                .imageUrl(imageUrl)
                .price(price)
                .rating(rating)
                .reviewCount(reviewCount)
                .ratingCount(ratingCount)
                .category(categoryPath)
                .build();
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.contains("trendyol.com");
    }

    @Override
    public List<Comment> commentScrap(String productUrl) {
        long contentId = extractContentId(productUrl);
        List<Comment> comments = new ArrayList<>();

        try {
            org.jsoup.Connection.Response initialResponse = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                    .execute();

            Map<String, String> cookies = initialResponse.cookies();

            int page = 0;
            int pageSize = 50;

            while (true) {
                List<Comment> pageComments = fetchCommentsPage(contentId, productUrl, cookies, page, pageSize);

                if (pageComments.isEmpty()) {
                    break;
                }

                comments.addAll(pageComments);
                page++;

                if (page >= MAX_COMMENT_PAGES) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error("Trendyol yorumları çekilirken hata oluştu. URL: {}", productUrl, e);
        }

        return formatComments(comments);
    }

    private List<Comment> fetchCommentsPage(
            long contentId,
            String productUrl,
            Map<String, String> cookies,
            int page,
            int pageSize
    ) {
        List<Comment> comments = new ArrayList<>();

        String url = String.format(
                "https://apigw.trendyol.com/discovery-storefront-trproductgw-service/api/review-read/product-reviews/detailed" +
                        "?contentId=%d&page=%d&pageSize=%d&channelId=1",
                contentId,
                page,
                pageSize
        );

        String jsonResponseStr;

        try {
            jsonResponseStr = Jsoup.connect(url)
                    .cookies(cookies)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                    .header("Accept-Language", "tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Referer", productUrl)
                    .header("Origin", "https://www.trendyol.com")
                    .ignoreContentType(true)
                    .timeout(10_000)
                    .execute()
                    .body();
        } catch (IOException e) {
            log.error("Yorum API'sine bağlanırken hata oluştu. Sayfa: {}, ContentId: {}", page, contentId, e);
            throw new ScrapingConnectionException("Yorum API'sine bağlanılamadı. Sayfa: " + page, e);
        }

        try {
            JsonNode root = objectMapper.readTree(jsonResponseStr);
            JsonNode result = require(root, "result");
            JsonNode reviews = require(result, "reviews");

            if (reviews.isArray()) {
                for (JsonNode review : reviews) {
                    String text = review.path("comment").asText("").trim();
                    if (text.isEmpty()) {
                        continue;
                    }

                    int rate = review.path("rate").asInt(0);
                    int likesCount = review.path("likesCount").asInt(0);

                    comments.add(new Comment(rate, text, likesCount));
                }
            }
        } catch (ProductParsingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Yorum JSON yanıtı parse edilemedi. Sayfa: {}, ContentId: {}", page, contentId, e);
            throw new ProductParsingException("Yorum verisi parse edilemedi. Sayfa: " + page, e);
        }

        return comments;
    }
    private List<Comment> formatComments(List<Comment> comments) {
        return comments.stream()
                // 1. Çok kısa / anlamsız yorumları ele (en az 15 karakter)
                .filter(c -> c.text() != null && c.text().trim().length() > 15)
                // 2. En çok beğeni alan ilk 70 yorumu seç (Temsil gücü en yüksek olanlar)
                .sorted(Comparator.comparingInt(Comment::likesCount).reversed())
                .limit(150)
                .toList();
    }

    @Override
    public long extractContentId(String productUrl) {
        try {
            String path = URI.create(productUrl).getPath();
            int index = path.lastIndexOf("-p-");

            if (index == -1) {
                throw new InvalidProductUrlException("Product ID URL içerisinde bulunamadı: " + productUrl);
            }

            String contentId = path.substring(index + 3);
            return Long.parseLong(contentId);

        } catch (IllegalArgumentException e) {
            throw new InvalidProductUrlException("Geçersiz ürün URL formatı: " + productUrl);
        }
    }

    @Override
    public JsonNode require(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new ProductParsingException("Trendyol response alanı parse edilemedi: " + field);
        }
        return value;
    }
}