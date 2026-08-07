package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.FeatureSentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureSentimentRepository extends JpaRepository<FeatureSentiment,Long> {
}
