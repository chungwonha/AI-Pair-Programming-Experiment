package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.BlogPost;
import com.cool.app.newssentimentanalysis.repository.BlogPostRepository;
import com.cool.app.newssentimentanalysis.repository.openai.ChatResponseRepository;
import com.cool.app.newssentimentanalysis.service.BloggerService;
import com.google.api.services.blogger.Blogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogposts")
public class BlogPostController {

    @Autowired
    private BlogPostRepository repository;

    private BloggerService bloggerService;
    private ChatResponseRepository chatResponseRepository;

    @Autowired
    public BlogPostController(BloggerService bloggerService, ChatResponseRepository chatResponseRepository) {

        this.bloggerService = bloggerService;
        this.chatResponseRepository = chatResponseRepository;
    }
    @PostMapping
    public ResponseEntity<BlogPost> createBlogPost(@RequestBody String text) {
        BlogPost blogPost = new BlogPost();
        blogPost.setText(text);
        blogPost = repository.save(blogPost);

        // Call method to post to Blogger
        bloggerService.postToBlogger(text);

        return new ResponseEntity<>(blogPost, HttpStatus.CREATED);
    }


}

