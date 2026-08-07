package com.berkaykomur.backend.scrapper;

import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.dto.ScrapperResult;

import java.util.List;

public interface Scrapper {

    ScrapperResult scrap(String productUrl);
    boolean supports(String url);
    List<Comment> commentScrap(String productUrl);

}
