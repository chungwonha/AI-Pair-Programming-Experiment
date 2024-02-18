package com.cool.app.newssentimentanalysis.repository.openai;

import com.cool.app.newssentimentanalysis.entity.openai.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
    // Standard CRUD methods are automatically provided.
    // You can add custom query methods here if needed.
}

