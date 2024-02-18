package com.cool.app.newssentimentanalysis.entity.openai;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_response_id")
    private ChatResponse chatResponse;

    private int index;

    @OneToOne(mappedBy = "choice", cascade = CascadeType.ALL)
    private Message message;

    private String logprobs; // Depending on its structure, this might need to be changed
    @Column(name = "finish_reason")
    private String finishReason;

    public void setMessage(Message message) {
        if (this.message != null && this.message.equals(message)) {
            return;
        }
        this.message = message;

        // Only set the back-reference if it's not already set
        if (message != null && message.getChoice() != this) {
            message.setChoice(this);
        }
    }

    public Message getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatResponse getChatResponse() {
        return chatResponse;
    }

    public void setChatResponse(ChatResponse chatResponse) {
        this.chatResponse = chatResponse;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(String logprobs) {
        this.logprobs = logprobs;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
}

