package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.WsjConsumerArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjEarningsArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjJobArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjTradeArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WsjTradeArticleRepository extends GenericArticleRepository<WsjTradeArticleEntity, Integer>{
    boolean existsByArticleLinksHref(String articleLinksHref);

    @Query("SELECT articleLinks FROM WsjTradeArticleEntity w WHERE w.articleLinksHref = :articleLinksHref")
    String findArticleTitleByArticleLinksHref(String articleLinksHref);
    @Query("SELECT w FROM WsjTradeArticleEntity w WHERE w.publishedDate IS NULL OR w.publishedDate = ''")
    Iterable<WsjTradeArticleEntity> findWithEmptyOrNullOrPublishedDate();
}
