package com.cool.app.newssentimentanalysis.repository;


import com.cool.app.newssentimentanalysis.entity.WsjGlobalEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjJobArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WsjJobArticleRepository extends GenericArticleRepository<WsjJobArticleEntity, Integer> {
    boolean existsByJobArticleHeadHref(String jobArticleHeadHref);
    @Query("SELECT jobArticleHead FROM WsjJobArticleEntity w WHERE w.jobArticleHeadHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(String articleLinksHref);
    @Query("SELECT w FROM WsjJobArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjJobArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
