package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feature_sentiment")
@Getter
@Setter
@NoArgsConstructor

public class FeatureSentiment extends BaseEntity{

    private String featureName;

    @Enumerated(EnumType.STRING)
    private LoveType loveType;

    private int percentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id",nullable = false)
    private Analysis analysis;



}
