package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "analysis_highlight")
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class AnalysisHighlight extends BaseEntity{

    private String AIComments;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "analysis_id")
    private Analysis analysis;


}
