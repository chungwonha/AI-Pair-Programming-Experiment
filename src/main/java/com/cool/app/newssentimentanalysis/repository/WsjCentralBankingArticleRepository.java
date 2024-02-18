package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjCentralBankingArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WsjCentralBankingArticleRepository extends GenericArticleRepository<WsjCentralBankingArticleEntity, Integer>{
    boolean existsByArticleLinksHref(String articleLinksHref);
    @Query("SELECT articleLinks FROM WsjCentralBankingArticleEntity w WHERE w.articleLinksHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(@Param("articleLinksHref") String articleLinksHref);
    @Query("SELECT w FROM WsjCentralBankingArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    List<WsjCentralBankingArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
