package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjEarningsArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WsjEarningsArticleRepository extends GenericArticleRepository<WsjEarningsArticleEntity, Integer>{
    boolean existsByArticleLinksHref(String articleLinksHref);
    @Query("SELECT articleLinks FROM WsjEarningsArticleEntity w WHERE w.articleLinksHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(String articleLinksHref);
    @Query("SELECT w FROM WsjEarningsArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjEarningsArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
