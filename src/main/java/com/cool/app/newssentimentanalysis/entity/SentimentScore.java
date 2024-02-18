package com.cool.app.newssentimentanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity@Data
public class SentimentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_identifier", unique = true)
    private String uniqueIdentifier;

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "article_title")
    private String articleTitle;

    @Column(name = "article_type")
    private String articleType;

    @Column(name = "article_source")
    private String articleSource;

    // Constructors
    public SentimentScore() {
    }

    public SentimentScore(String uniqueIdentifier, Double sentimentScore) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.sentimentScore = sentimentScore;
    }

}
