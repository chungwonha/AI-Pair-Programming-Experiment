package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjConsumerArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WsjConsumerArticleRepository extends GenericArticleRepository<WsjConsumerArticleEntity, Integer>{
    boolean existsByArticleHeadHref(String articleHeadHref);
    @Query("SELECT articleHead FROM WsjConsumerArticleEntity w WHERE w.articleHeadHref = :articleHeadHref")
    String findArticleHeadByArticleHeadHref(String articleHeadHref);
    @Query("SELECT w FROM WsjConsumerArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjConsumerArticleEntity> findWithEmptyOrNullOrPublishedDate();

}
