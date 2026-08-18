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
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL,orphanRemoval = true)
    private Analysis analyses; //Liste olmasını şuan kullanmıyorum


}
