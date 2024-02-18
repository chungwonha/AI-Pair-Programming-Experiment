package com.cool.app.newssentimentanalysis.entity;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity@Data@NoArgsConstructor
public class WsjHousingArticleEntity extends WsjArticleEntity implements Analyzable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_type")
    private String articleType = "Housing";

    @CsvBindByName(column = "web-scraper-order")
    private String webScraperOrder;

    @CsvBindByName(column = "web-scraper-start-url")
    private String webScraperStartUrl;

    @CsvBindByName(column = "housing_article_head")
    private String housingArticleHead;

    @CsvBindByName(column = "housing_article_head-href")
    private String housingArticleHeadHref;

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

}
