package com.berkaykomur.backend.ai.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.Comment;
import com.berkaykomur.backend.scrapper.Scrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AiAnalysisImpl implements AiAnalysis {
    private final ChatClient chatClient;

    public AiAnalysisImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public AnalysisResult analyzeComments(Scrapper scrapper, String productUrl){
        try {
            List<Comment> comments = scrapper.commentScrap(productUrl);
            if (comments.isEmpty()) {
                return null;
            }
            return analyze(comments);

        } catch (Exception e) {
            log.error("Analiz sırasında hata oluştu", e);
            return null;
        }
    }
    private AnalysisResult analyze(List<Comment> comments) {
        String prompt = """
        Analyze the following product reviews and generate a structured product analysis.

        All output text must be written in Turkish.

        Provide the following:

        1. aiScore
        - Give the product an overall score between 0.0 and 5.0.
        - Base the score only on the opinions expressed in the reviews.
        - Consider overall customer satisfaction, product quality, common complaints,
          and frequently mentioned positive aspects.

        2. summary
        - Write a short and objective summary of the overall customer opinion.
        - Mention the most important strengths and weaknesses.
        - Do not invent information that is not present in the reviews.

        3. topPositiveComment
        - Select the most useful and representative positive comment from the reviews.
        - Prefer comments that provide specific information about the product.
        - Do not rewrite or invent the comment.

        4. topNegativeComment
        - Select the most useful and representative negative comment from the reviews.
        - Prefer comments that provide specific information about the product.
        - Do not rewrite or invent the comment.

        5. highlights
        - Identify important recurring positive and negative points mentioned in the reviews.
        - Each highlight must have:
          - aiComments: a short description of the point
          - commentType: either PRO or CON
        - PRO must represent a positive aspect.
        - CON must represent a negative aspect.
        - Do not include points that are not supported by the reviews.
        - Prioritize frequently mentioned and meaningful aspects.
        - Avoid duplicates.

        6. featureResults
        - Identify important product features discussed in the reviews.
        - Analyze the sentiment associated with each feature.
        - Only include features that are actually mentioned in the reviews.

        General rules:
        - Base the entire analysis only on the provided reviews.
        - Do not hallucinate or assume product specifications.
        - Focus on recurring opinions rather than isolated comments.
        - Keep the analysis concise and useful for a product comparison dashboard.
        - Return the result in the exact structure expected by the application.

        Reviews:
        %s
        """.formatted(formatComments(comments));

        try {
            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(AnalysisResult.class);
        } catch (Exception e) {
            log.error("AI analizi sonucunda hata oluştu: {}", e.getMessage());
            return null;
        }
    }

    private String formatComments(List<Comment> comments) {
        return comments.stream()
                .map(Comment::text)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

}
