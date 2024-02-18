package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WashingtonPostEconomyArticleEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WashingtonPostEconomyArticleRepository  extends GenericArticleRepository<WashingtonPostEconomyArticleEntity, Integer> {
    boolean existsByArticleLinksHref(String articleLinksHref);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.washington_post_economy_article_entity SET published_date = TO_CHAR(TO_TIMESTAMP(published_date, 'Month DD, YYYY at HH:MI a.m. \"EST\"'), 'Mon-DD-YYYY') WHERE published_date IS NOT NULL AND published_date != ''", nativeQuery = true)
    void updatePublishedDateFormats();
}
