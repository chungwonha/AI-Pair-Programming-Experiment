package com.cool.app.newssentimentanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    // Getters and setters
}
