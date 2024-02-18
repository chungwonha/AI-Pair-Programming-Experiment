package com.cool.app.newssentimentanalysis.controller;

// ... other imports

import com.cool.app.newssentimentanalysis.service.ChatResponseService;
import com.cool.app.newssentimentanalysis.service.OpenAIService;
import com.cool.app.newssentimentanalysis.service.S3Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBaseCsvController<T, ID, R extends JpaRepository<T, ID>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseCsvController.class);

    @Autowired
    protected R repository;

    protected abstract Class<T> getEntityClass();
    protected abstract String getUniqueAttributeValue(T entity);
    protected abstract boolean checkArticleExists(String uniqueAttributeValue);

    protected String loadCsvData(String csvFolder) {
        File folder = new File(csvFolder);
        File[] listOfFiles = folder.listFiles();
        logger.info("csvFolder: "+csvFolder);

        logger.info("listOfFiles.length: "+listOfFiles.length);
        List<String> csvFileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
//                String csvFilePath = file.getPath()+"\\"+file.getName();
                logger.info("csvFilePath: "+file.getName());
                try (Reader reader = Files.newBufferedReader(Path.of(file.getPath()))) {
                    CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                            .withType(getEntityClass())
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();

                    List<T> entities = csvToBean.parse();

                    for (T entity : entities) {
                        String uniqueAttributeValue = getUniqueAttributeValue(entity);
                        if (!checkArticleExists(uniqueAttributeValue)) {
                            repository.save(entity);
                            logger.info("Saved new entity from file: " + file.getName());
                        } else {
                            logger.info("Entity already exists and was not saved again: " + uniqueAttributeValue);
                        }
                    }
                    logger.info(file.getPath() +" - Data successfully loaded!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error loading data: " + e.getMessage();
                }
            }
        }
        return "All Data successfully loaded!";
    }
}
