package com.cool.app.newssentimentanalysis.entity;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@Entity
public class WsjJobArticleEntity extends WsjArticleEntity implements Analyzable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_type")
    private String articleType = "Job";

    @CsvBindByName(column = "web-scraper-order")
    private String webScraperOrder;

    @CsvBindByName(column = "web-scraper-start-url")
    private String webScraperStartUrl;

    @CsvBindByName(column = "job_article_head")
    private String jobArticleHead;

    @CsvBindByName(column = "job_article_head-href")
    private String jobArticleHeadHref;

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
