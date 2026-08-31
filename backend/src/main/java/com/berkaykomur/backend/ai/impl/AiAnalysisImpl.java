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
                
                IMPORTANT:
                - The reviews were selected to represent both highly positive and highly negative customer experiences.
                - Do not assume that the provided reviews represent the exact overall rating distribution of the product.
                - Do not calculate the product score simply by averaging the star ratings.
                - Evaluate the actual content of the reviews and the consistency of the opinions.
                - Star rating, review text, and likesCount should be considered together.
                
                Provide the following:
                
                1. aiScore
                - Give the product an overall score between 0.0 and 5.0.
                - Base the score only on the opinions expressed in the provided reviews.
                - Do NOT simply calculate the mathematical average of the star ratings.
                - Consider:
                  - overall customer satisfaction
                  - product effectiveness
                  - product quality
                  - frequently mentioned positive aspects
                  - frequently mentioned complaints
                  - consistency of customer experiences
                  - severity of negative experiences
                - Give more importance to recurring opinions than isolated opinions.
                - A detailed review with specific information can be more informative than a very short review.
                - likesCount can be used as a supporting signal for how useful or representative a review may be, but it must NOT be treated as proof that the review is true.
                - Do not infer information that is not present in the reviews.
                
                2. summary
                - Write a short, objective, and balanced summary of the overall customer opinion.
                - Mention the most important strengths and weaknesses.
                - Clearly distinguish between positive and negative experiences when opinions are mixed.
                - If customers have significantly different experiences, mention this instead of presenting the product as universally good or bad.
                - Do not invent information that is not present in the reviews.
                - Do not make medical, scientific, or technical claims that are not explicitly supported by the reviews.
                
                3. topPositiveComment
                - Select the most useful, informative, and representative positive comment from the reviews.
                - Prefer comments that:
                  - describe a specific product experience
                  - explain why the customer liked the product
                  - mention a noticeable result or benefit
                  - provide more information than a generic "çok güzel" comment
                - Prefer detailed and meaningful comments over very short comments.
                - likesCount may be considered as a supporting signal when choosing between similarly useful comments.
                - Return the original comment exactly as provided.
                - Do not rewrite, shorten, summarize, or invent the comment.
                
                4. topNegativeComment
                - Select the most useful, informative, and representative negative comment from the reviews.
                - Prefer comments that:
                  - describe a specific problem
                  - explain why the customer was dissatisfied
                  - describe a product-related issue or negative experience
                  - provide more information than a generic "kötü" comment
                - Prefer detailed and meaningful comments over very short comments.
                - likesCount may be considered as a supporting signal when choosing between similarly useful comments.
                - Return the original comment exactly as provided.
                - Do not rewrite, shorten, summarize, or invent the comment.
                
                5. highlights
                - Identify important recurring positive and negative points mentioned in the reviews.
                - Each highlight must have:
                  - aiComments: a short and clear description of the point
                  - commentType: either PRO or CON
                - PRO must represent a positive aspect.
                - CON must represent a negative aspect.
                - Only include points that are supported by the reviews.
                - Prioritize recurring and meaningful themes.
                - Multiple reviews expressing the same underlying issue should be treated as one recurring theme rather than separate highlights.
                - Avoid duplicate or semantically identical highlights.
                - Do not create a highlight from a single vague comment unless the comment describes a particularly important or severe experience.
                - Distinguish product-related problems from seller, shipping, packaging, delivery, or customer-service problems when possible.
                - Do not automatically treat seller or shipping problems as defects of the product itself.
                
                Examples of possible categories:
                - product effectiveness
                - product quality
                - ease of use
                - irritation or unwanted effects reported by customers
                - packaging
                - delivery
                - incorrect or missing products
                - seller/customer service
                - value for money
                
                6. featureResults
                - Identify important product features, characteristics, benefits, or problems explicitly discussed in the reviews.
                - Analyze the sentiment associated with each feature.
                - Only include features that are actually mentioned in the reviews.
                - Do not invent product specifications or features.
                - Group similar mentions under the same feature.
                - If a feature has both positive and negative opinions, reflect the mixed sentiment accurately.
                - Focus on meaningful features that would be useful when comparing this product with other products.
                
                General rules:
                - Base the entire analysis ONLY on the provided reviews.
                - Do not use external knowledge.
                - Do not hallucinate or assume product specifications.
- Do not assume that a customer's claim is objectively true; report it as a customer experience or opinion.
- Star ratings are signals, but the written content is more important for understanding the reason behind the rating.
- Consider likesCount as a supporting signal only. A high likesCount does not prove that a claim is objectively correct.
- Focus on recurring opinions and meaningful experiences rather than isolated generic comments.
- Do not let multiple nearly identical short comments dominate the analysis simply because they are repeated.
- Distinguish between:
  1. product performance/quality
  2. seller/service issues
  3. shipping/delivery issues
  4. packaging issues
- If the reviews contain conflicting opinions, explicitly reflect the disagreement instead of choosing one side without evidence.
- Do not make claims beyond what the reviews support.
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
            log.error("AI analizi sonucunda hata oluştu: {}", e);

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
