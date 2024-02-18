package com.cool.app.newssentimentanalysis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
public abstract class WsjArticleEntity {

    @Column(name = "article_source")
    private String articleSource="WSJ";


}
