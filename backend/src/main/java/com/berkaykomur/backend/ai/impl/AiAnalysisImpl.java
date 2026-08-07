package com.berkaykomur.backend.ai.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.scrapper.Scrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiAnalysisImpl implements AiAnalysis {

    @Async
    @Override
    public void analyzeComments(Scrapper scrapper,String productUrl){
        List<Comment> comments=scrapper.commentScrap(productUrl);
        if(comments.isEmpty()){
            return;
        }

    }

}
