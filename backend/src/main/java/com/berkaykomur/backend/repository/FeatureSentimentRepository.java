package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.FeatureSentiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureSentimentRepository extends JpaRepository<FeatureSentiment,Long> {
}
