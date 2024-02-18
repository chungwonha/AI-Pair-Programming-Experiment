package com.cool.app.newssentimentanalysis.entity.openai;

import jakarta.persistence.*;
import lombok.Data;



@Entity

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "choice_id")
    private Choice choice;

    private String role;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name="s3etag")
    private String s3etag;

    @Column(name="s3key")
    private String s3Key;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getS3etag() {
        return s3etag;
    }

    public void setS3etag(String s3etag) {
        this.s3etag = s3etag;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        if (this.choice != null && this.choice.equals(choice)) {
            return;
        }
        this.choice = choice;

        // Only set the back-reference if it's not already set
        if (choice != null && choice.getMessage() != this) {
            choice.setMessage(this);
        }
    }
}

