package com.berkaykomur.backend.scrapper;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ScrapperResult;
import tools.jackson.databind.JsonNode;

import java.util.List;

public interface Scrapper {

    ScrapperResult scrap(String productUrl);
    boolean supports(String url);
    List<Comment> commentScrap(String productUrl);
    long extractContentId(String productUrl);
    JsonNode require(JsonNode node, String field);

}
