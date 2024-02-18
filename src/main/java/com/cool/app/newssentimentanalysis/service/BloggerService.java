package com.cool.app.newssentimentanalysis.service;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Post;
import com.google.api.client.auth.oauth2.Credential;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
@Service
public class BloggerService {

    private static final String APPLICATION_NAME = "trendsentryai";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\chung\\codes\\trendsentryai-credential\\trendsentryai-36fed2b3cfa3.json";
    public void postToBlogger(String text) {
        // Initialize the Blogger service with OAuth credentials
        Blogger bloggerService = getBloggerService();

        // Create and configure a new post
        Post newPost = new Post();
        newPost.setTitle("New Post");
        newPost.setContent(text);

        try {
            // Insert the post
            bloggerService.posts().insert("TrendSentry123", newPost).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Blogger getBloggerService() {

        try {
        // Set up authorization and return a Blogger service object
        // Refer to Google's Blogger API documentation for the detailed implementation
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(
                        BloggerService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)));


            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JacksonFactory.getDefaultInstance(), clientSecrets,
                    Collections.singleton(BloggerScopes.BLOGGER))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();


        // Run authorization flow and store credential
        // TODO: Implement the OAuth2 flow for obtaining the access token

            // Load credential from storage or through Google's OAuth 2.0 flow
            Credential credential = new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("chunghaster@gmail.com");

        Blogger blogger = new Blogger.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return blogger;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
//        return null;
    }


}
