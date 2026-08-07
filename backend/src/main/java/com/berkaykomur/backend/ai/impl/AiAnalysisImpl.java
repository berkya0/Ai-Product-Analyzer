package com.berkaykomur.backend.ai.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.scrapper.Scrapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiAnalysisImpl implements AiAnalysis {
    private final ChatClient chatClient;

    public AiAnalysisImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public AnalysisResult analyzeComments(Scrapper scrapper,String productUrl){
        List<Comment> comments=scrapper.commentScrap(productUrl);
        if(comments.isEmpty()){
            return null;
        }
        return analyze(comments);
    }
    private AnalysisResult analyze(List<Comment> comments) {
        String prompt = """
                Analyze the following product reviews.

                Provide:
                - An overall AI score between 0 and 5
                - A short summary
                - The most positive comment
                - The most negative comment
                - Important positive and negative highlights
                - Sentiment analysis for important product features

                Reviews:
                %s
                """.formatted(formatComments(comments));

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AnalysisResult.class);
    }

    private String formatComments(List<Comment> comments) {
        return comments.stream()
                .map(Comment::text)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

}
