package com.berkaykomur.backend.scrapper;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResponse;

import java.util.List;

public interface Scrapper {

    ScrapperResponse scrap(String productUrl);
    boolean supports(String url);
    List<Comment> commentScrap(String productUrl);

}
