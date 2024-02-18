package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
}

