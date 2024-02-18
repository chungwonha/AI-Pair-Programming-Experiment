package com.cool.app.newssentimentanalysis.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data@NoArgsConstructor
public class AnalyticalResults {

    private String articleId;
    private String analyticalResults;
    private int positiveCounts;
    private int neutralCounts;
    private int negativeCounts;
    private int veryPositiveCounts;
    private int veryNegativeCounts;
    private Date pubDate;
    private double score;
    private  String p_n;
}
