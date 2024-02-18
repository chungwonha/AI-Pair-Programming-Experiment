package com.cool.app.newssentimentanalysis.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BloggerServiceTest {

    @Autowired
    private BloggerService bloggerService;

    @Test
    void testPostToBlogger() {
        String text = "This is a test blog post.";
        //bloggerService.postToBlogger(text);
    }
}
