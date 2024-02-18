package com.cool.app.newssentimentanalysis.controller;

import org.springframework.web.client.RestTemplate;

public class ApplicationHealth {
    public String health() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8080/actuator/health", String.class);

        return "OK";
    }

}
