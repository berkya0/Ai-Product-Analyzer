package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByProductUrl(String url);

    @Modifying
    @Query(
            value = "UPDATE product SET is_deleted = false WHERE id = :id",
            nativeQuery = true
    )
    void restoreProduct(@Param("id") Long id);

    @Query(
            value = "SELECT * FROM product WHERE product_url = :productUrl",
            nativeQuery = true
    )
    Optional<Product> findProductIncludingDeleted(@Param("productUrl") String productUrl);
}
