package com.berkaykomur.backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@SoftDelete(columnName = "is_deleted")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;
    private String imageUrl;

    @Column(nullable = false,unique = true)
    private String productUrl;

    private Double rating;

    @Column(nullable = false)
    private BigDecimal price;
    private Integer reviewCount;
    private Integer ratingCount;

    @Builder.Default
    private boolean isFollowing=false;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Analysis> analyses=new ArrayList<>();

    public void addAnalysis(Analysis analysis){
        analyses.add(analysis);
        analysis.setProduct(this);

    }
    public void removeAnalysis(Analysis analysis){
        analyses.remove(analysis);
        analysis.setProduct(null);

    }

}
