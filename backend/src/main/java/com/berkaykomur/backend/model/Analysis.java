package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analysis")
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class Analysis extends BaseEntity {

    private Double aiScore;
    private String summary;
    @Enumerated(EnumType.STRING)
    private Status status=Status.PENDING;

    private String topPositiveComment;
    private String topNegativeComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<AnalysisHighlight> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<FeatureSentiment> featureSentiments = new ArrayList<>();

    public void addHighlight(AnalysisHighlight highlight) {
        highlights.add(highlight);
        highlight.setAnalysis(this);
    }
    public void removeHighlight(AnalysisHighlight highlight) {
        highlights.remove(highlight);
        highlight.setAnalysis(null);
    }
    public void addFeatureSentiment(FeatureSentiment featureSentiment) {
        featureSentiments.add(featureSentiment);
        featureSentiment.setAnalysis(this);
    }
    public void removeFeatureSentiment(FeatureSentiment featureSentiment) {
        featureSentiments.remove(featureSentiment);
        featureSentiment.setAnalysis(null);
    }

}
