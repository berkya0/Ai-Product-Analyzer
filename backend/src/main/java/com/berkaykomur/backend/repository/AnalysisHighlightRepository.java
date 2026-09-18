package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.AnalysisHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisHighlightRepository extends JpaRepository<AnalysisHighlight,Long> {
}
