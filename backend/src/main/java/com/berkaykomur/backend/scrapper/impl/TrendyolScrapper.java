package com.berkaykomur.backend.scrapper.impl;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ScrapperResult;
import com.berkaykomur.backend.exception.InvalidProductUrlException;
import com.berkaykomur.backend.exception.JsonLdNotFoundException;
import com.berkaykomur.backend.exception.ProductParsingException;
import com.berkaykomur.backend.exception.ScrapingConnectionException;
import com.berkaykomur.backend.scrapper.Scrapper;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendyolScrapper implements Scrapper {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ScrapperResult scrap(String productUrl) {
        Document document;
        try {
            document = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new ScrapingConnectionException(e.getMessage());
        }

        Element jsonLdScript = document.selectFirst("script[type=application/ld+json]");
        if (jsonLdScript == null) {
            throw new JsonLdNotFoundException("");
        }
        JsonNode root;
        root = objectMapper.readTree(jsonLdScript.data());

        JsonNode offers = require(root, "offers");
        JsonNode aggregateRating = require(root, "aggregateRating");
        JsonNode image = require(root, "image");
        JsonNode contentUrls = require(image, "contentUrl");

        if (!contentUrls.isArray() || contentUrls.isEmpty()) {
            throw new ProductParsingException("Ürün resmi çekilemedi ."+contentUrls);
        }

        String name = require(root, "name").asText();

        BigDecimal price = new BigDecimal(
                require(offers, "price").asText()
        );
        String imageUrl = contentUrls.get(0).asText();
        int ratingCount = require(aggregateRating, "ratingCount").asInt();
        int reviewCount = require(aggregateRating, "reviewCount").asInt();
        double rating = require(aggregateRating, "ratingValue").asDouble();

        return ScrapperResult.builder()
                .name(name)
                .productUrl(productUrl)
                .imageUrl(imageUrl)
                .price(price)
                .rating(rating)
                .reviewCount(reviewCount)
                .ratingCount(ratingCount)
                .build();
    }
    @Override
    public boolean supports(String url) {
        return url.contains("trendyol.com");
    }

    @Override
    public List<Comment> commentScrap(String productUrl) {
        long contentId = extractContentId(productUrl);
        RestClient restClient = RestClient.create();
        List<Comment> comments = new ArrayList<>();

        int page = 0;
        int totalPages = 1;
     
        do {
            String url =
                    "https://apigw.trendyol.com/discovery-storefront-trproductgw-service/api/review-read/product-reviews/detailed"
                            + "?contentId=" + contentId
                            + "&page=" + page
                            + "&pageSize=10"
                            + "&channelId=1";
            JsonNode root = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode result = require(root, "result");
            if (page == 0) {
                JsonNode summary = require(result, "summary");
                JsonNode totalPagesNode = summary.get("totalPages");
                totalPages = totalPagesNode.asInt();
            }
            JsonNode reviews = require(result, "reviews");
            if (reviews.isEmpty()) {
                break;
            }
            for (JsonNode review : reviews) {
                comments.add(new Comment(
                        require(review, "rate").asInt(),
                        require(review, "comment").asText(),
                        require(review, "likesCount").asInt()
                ));
            }
            page++;
        } while (page < totalPages);

        return comments;
    }

    private long extractContentId(String productUrl) {
        try {
            String path = URI.create(productUrl).getPath();
            int index = path.lastIndexOf("-p-");

            if (index == -1) {
                throw new InvalidProductUrlException("");
            }

            String contentId = path.substring(index + 3);

            return Long.parseLong(contentId);

        } catch (IllegalArgumentException e) {
            throw new InvalidProductUrlException("");
        }
    }
    private JsonNode require(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new ProductParsingException(
                    "Trendyol response alanı parse edilemedi: " + field
            );
        }
        return value;
    }
}
