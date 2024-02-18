package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjEarningsArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjGlobalEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjTradeArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WsjGlobalEconomyArticleRepository extends GenericArticleRepository<WsjGlobalEconomyArticleEntity, Integer>{
    boolean existsByArticleLinksHref(String articleLinksHref);

    @Query("SELECT articleLinks FROM WsjGlobalEconomyArticleEntity w WHERE w.articleLinksHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(String articleLinksHref);
    @Query("SELECT w FROM WsjGlobalEconomyArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjGlobalEconomyArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
