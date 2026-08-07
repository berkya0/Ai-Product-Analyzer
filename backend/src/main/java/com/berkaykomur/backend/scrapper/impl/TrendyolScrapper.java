package com.berkaykomur.backend.scrapper.impl;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResponse;
import com.berkaykomur.backend.exception.InvalidProductUrlException;
import com.berkaykomur.backend.exception.JsonLdNotFoundException;
import com.berkaykomur.backend.exception.ProductParsingException;
import com.berkaykomur.backend.exception.ScrapingConnectionException;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendyolScrapper implements Scrapper {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ScrapperResponse scrap(String productUrl) {
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
        try {
            root = objectMapper.readTree(jsonLdScript.data());
        } catch (IOException e) {
            throw new ProductParsingException("");
        }

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

        return ScrapperResponse.builder()
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
        int totalPages;
        do {
            String url =
                    "https://apigw.trendyol.com/discovery-storefront-trproductgw-service/api/review-read/product-reviews/detailed"
                            + "?contentId=" + contentId
                            + "&page=" + page
                            + "&pageSize=100"
                            + "&channelId=1";

            JsonNode root = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode result = require(root, "result");
            totalPages = require(
                    require(result, "summary"),
                    "totalPages"
            ).asInt();

            JsonNode reviews = require(result, "reviews");

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

    private long extractContentId(String productUrl){
        int index= productUrl.lastIndexOf("-p-");
        if(index==-1){
            throw new InvalidProductUrlException("");
        }
        try{
          return  Long.parseLong(
                    productUrl.substring(index + 3)
            );
        }
        catch (NumberFormatException e){
            throw new InvalidProductUrlException("");
        }

    }
    private JsonNode require(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new ProductParsingException(
                    "Product alanı parse edilemedi: " + field
            );
        }
        return value;
    }
}
