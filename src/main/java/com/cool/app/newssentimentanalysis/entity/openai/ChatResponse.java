package com.cool.app.newssentimentanalysis.entity.openai;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity@Data
public class ChatResponse {
    @Id
    private String id;
    private String object;
    private long created;
    private String model;

    @OneToMany(mappedBy = "chatResponse", cascade = CascadeType.ALL)
    private List<Choice> choices;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_id", referencedColumnName = "id")
    private Usage usage;

    private String systemFingerprint;

    private String uniqueIdentifier;
}
