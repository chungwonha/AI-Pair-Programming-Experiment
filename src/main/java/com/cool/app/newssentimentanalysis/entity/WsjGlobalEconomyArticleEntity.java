package com.cool.app.newssentimentanalysis.entity;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor// Lombok annotation to create getters, setters, toString, equals, and hashCode methods
public class WsjGlobalEconomyArticleEntity extends WsjArticleEntity implements Analyzable{

///
    //web-scraper-order,web-scraper-start-url,article_links,article_links-href,article_text,published_date
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_type")
    private String articleType="Global Economy";

    @CsvBindByName(column = "web-scraper-order")
    private String webScraperOrder;

    @CsvBindByName(column = "web-scraper-start-url")
    private String webScraperStartUrl;

    @CsvBindByName(column = "article_links")
    private String articleLinks;

    @CsvBindByName(column = "article_links-href")
    private String articleLinksHref;

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

