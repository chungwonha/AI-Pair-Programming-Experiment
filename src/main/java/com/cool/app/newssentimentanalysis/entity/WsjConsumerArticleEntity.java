package com.cool.app.newssentimentanalysis.entity;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor// Lombok annotation to create getters, setters, toString, equals, and hashCode methods
public class WsjConsumerArticleEntity extends WsjArticleEntity implements Analyzable{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_type")
    final private String articleType="Consumer";

    @CsvBindByName(column = "web-scraper-order")
    private String webScraperOrder;

    @CsvBindByName(column = "web-scraper-start-url")
    private String webScraperStartUrl;

    @CsvBindByName(column = "article_head")
    private String articleHead;

    @CsvBindByName(column = "article_head-href")
    private String articleHeadHref;

    @CsvBindByName(column = "published_date")
    private String publishedDate;

    @Lob
    @CsvBindByName(column = "article_text")
    @Column(name = "article_text", columnDefinition = "TEXT")
    private String articleText;

    @Override
    public String getTextForAnalysis() {
        return this.articleText;
    }

    @Override
    public int getId() {
        return id;
    }

}

