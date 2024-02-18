package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjGlobalEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjHousingArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WsjHousingArticleRepository extends GenericArticleRepository<WsjHousingArticleEntity, Integer> {
    boolean existsByHousingArticleHeadHref(String housingArticleHeadHref);

    @Query("SELECT housingArticleHead FROM WsjHousingArticleEntity w WHERE w.housingArticleHeadHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(String articleLinksHref);
    @Query("SELECT w FROM WsjHousingArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjHousingArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
