package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "analysis_highlight")
@NoArgsConstructor
@Setter
@Getter
public class AnalysisHighlight extends BaseEntity{

    private String AIComments;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "analysis_id")
    private Analysis analysis;

}
