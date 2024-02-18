package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.entity.openai.ChatResponse;
import com.cool.app.newssentimentanalysis.entity.openai.Choice;
import com.cool.app.newssentimentanalysis.entity.openai.Message;
import com.cool.app.newssentimentanalysis.repository.openai.ChatResponseRepository;
import com.cool.app.newssentimentanalysis.repository.openai.ContentRepository;
import com.cool.app.newssentimentanalysis.repository.openai.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service@Slf4j
public class ChatResponseService {

    Logger logger = org.slf4j.LoggerFactory.getLogger(ChatResponseService.class);

    private final ChatResponseRepository chatResponseRepository;
    private final ContentRepository contentRepository;
    private final ObjectMapper objectMapper;

    private final MessageRepository messageRepository;

//    @Autowired  -> not necessary to use the @Autowired annotation on a class constructor if the class has only one constructor if using Spring 4.3 or later .
    public ChatResponseService(ChatResponseRepository chatResponseRepository, ObjectMapper objectMapper, ContentRepository contentRepository, MessageRepository messageRepository) {
        this.chatResponseRepository = chatResponseRepository;
        this.objectMapper = objectMapper;
        this.contentRepository = contentRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void saveChatResponse(String jsonResponse,String uniqueIdentifier) throws Exception {
        // Parse the JSON response into the ChatResponse object
        ChatResponse chatResponse = objectMapper.readValue(jsonResponse, ChatResponse.class);
        chatResponse.setUniqueIdentifier(uniqueIdentifier);
        // Iterate through the choices and set the back-reference to the ChatResponse
        if (chatResponse.getChoices() != null) {
            for (Choice choice : chatResponse.getChoices()) {
                choice.setChatResponse(chatResponse);

                // If Message is present, set the back-reference to the Choice
                if (choice.getMessage() != null) {
                    Message message = choice.getMessage();
                    message.setChoice(choice);

                    String jsonContent = message.getContent();
                    logger.info("jsonContent: "+jsonContent);

//                    JsonNode contentNode = objectMapper.readTree(jsonContent);
//                    Content content = objectMapper.treeToValue(contentNode, Content.class); // where jsonString is the actual JSON object, not a string literal
//
//                    logger.info("Summary: "+content.getSummary());
//                    logger.info("Sentiment: "+content.getSentimentScore());
//                    logger.info("RapTitle: "+content.getRapTitle());
//                    logger.info("RapLyrics: "+content.getRapLyrics());
//
//                    contentRepository.save(content);


                }
            }
        }
        logger.info(" Before saving chatResponse ");
        // Save the ChatResponse entity along with its nested Choice and Message entities
        chatResponseRepository.save(chatResponse);
    }

    public List<ChatResponse> getAllChatResponses() {
        return chatResponseRepository.findAll();
    }

    public String getContent(String id) {
        if(chatResponseRepository.findById(id).isPresent()){
            ChatResponse chatResponse = chatResponseRepository.findById(id).get();
            if(chatResponse.getChoices().size() > 0) {
                Choice choice = chatResponse.getChoices().get(0);
                if(choice.getMessage()!=null) {
                    Message message = choice.getMessage();
                    if(message.getContent() != null) {
                        return message.getContent();
                    }
                }
            }
        }
        return null;
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}

