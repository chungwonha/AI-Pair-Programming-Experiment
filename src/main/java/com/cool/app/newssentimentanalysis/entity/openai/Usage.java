package com.cool.app.newssentimentanalysis.entity.openai;

import jakarta.persistence.*;

@Entity
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prompt_tokens")
    private int promptTokens;

    @Column(name = "completion_tokens")
    private int completionTokens;

    @Column(name = "total_tokens")
    private int totalTokens;

    @OneToOne(mappedBy = "usage", fetch = FetchType.LAZY)
    private ChatResponse chatResponse;

    // Getters and setters
}

