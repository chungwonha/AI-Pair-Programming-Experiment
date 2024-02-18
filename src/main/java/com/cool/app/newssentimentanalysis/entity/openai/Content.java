package com.cool.app.newssentimentanalysis.entity.openai;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity@Data@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "choice_id")
//    private Choice choice;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name="sentiment_score")
    private String sentimentScore;
    @Column(name="rap_title")
    private String rapTitle;
    @Column(name="rap_lyrics")
    private String rapLyrics;

//     Assuming these fields map to the JSON structure in the `content` field
//    @OneToOne(mappedBy = "content", fetch = FetchType.LAZY)
//    private Message message;

}

