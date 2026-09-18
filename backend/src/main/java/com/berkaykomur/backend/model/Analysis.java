package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.Builder;
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
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status=Status.PENDING;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String topPositiveComment;

    @Column(columnDefinition = "TEXT")
    private String topNegativeComment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false,unique = true)
    private Product product;


    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @Builder.Default
    private List<AnalysisHighlight> highlights = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<FeatureSentiment> featureSentiments = new ArrayList<>();

}
