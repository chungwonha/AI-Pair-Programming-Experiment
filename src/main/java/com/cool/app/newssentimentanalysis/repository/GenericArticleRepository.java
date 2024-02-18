package com.cool.app.newssentimentanalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface GenericArticleRepository<T, ID> extends JpaRepository<T, ID> {
//    boolean existsByUniqueAttribute(String uniqueAttributeValue);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.publishedDate = REPLACE(e.publishedDate, 'Updated ', '') WHERE e.publishedDate LIKE 'Updated %'")
    void updateWsjPublishedDates();

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.publishedDate = :newDate WHERE e.id = :id")
    void updatePublishedDateById(ID id, String newDate);
}

