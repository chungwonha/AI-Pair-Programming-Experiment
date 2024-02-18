package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.service.ChatResponseService;
import com.cool.app.newssentimentanalysis.service.OpenAIService;
import com.cool.app.newssentimentanalysis.service.S3Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatGptController {
    Logger logger = org.slf4j.LoggerFactory.getLogger(ChatGptController.class);

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    ChatResponseService chatResponseService;
    @Autowired
    private S3Service s3Service;
    private final ObjectMapper objectMapper;
    @Value("${openai.api.response.html-path}")
    private String openaiResponseHtmlOutputPath;


    public ChatGptController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @PostMapping("/newsarticlesummary")
    public String chatWithOpenAIForNewsArticleSummary(@RequestBody String newsarticle) {
        openAIService.chatForNewsArticleSummary(newsarticle,"newsarticlesummary");
        return "chatWithOpenAIForNewsArticleSummary complete";
    }


    @PostMapping("/general")
    public String chatWithOpenAI(@RequestBody String prompt) {
        return openAIService.getChatResponse(prompt);
    }

    @GetMapping("/chatresponse/{id}/message")
    public String getChatResponseMessage(@PathVariable String id) {
        return chatResponseService.getContent(id);
    }

    @GetMapping("/chatresponse/{id}/generate-html-upload")
    public String generateHtmlFromJsonAndUploadToS3(@PathVariable String id) {
//        String json = "{ \"summary\": \"Following the addition of 336,000 jobs in September, signs of an accelerating U.S. economy have sparked a bond market selloff, with longer-term borrowing rates reaching 16-year highs. The strong jobs report has lowered the likelihood of a recession and subsequent Fed rate cuts. Investors reacted nervously initially but the markets recovered later. The jobs growth significantly surpassed economic forecasts, despite rising oil prices, interest rates, and inflation. This robust jobs growth, driven partly by robust consumer spending, could complicate matters for the Federal Reserve, which held its key interest rate steady last month.\", \"sentiment_score\": 0.7, \"rap_title\": \"Bonds on the Run, Jobs Having Fun\", \"rap_lyrics\": \"Yo, it's the US economy on a ride,\\nBurstin' jobs, investors can't hide.\\nBond market sellin' off, rates hit the sky, \\nRecession fears sayin' bye-bye!\\nInflation hittin’, oil prices liftin',\\nBut job growth ain't shiftin'.\\nFed's got decisions, rates on a mission,\\nEconomy in transition, that's the vision!\", \"labels\": \"US economy, Job growth, Bond market, Federal Reserve, Interest rates, Recession, Inflation, Oil Prices\", \"cartoon\": \"Cartoon of the U.S economy as a speeding train, filled with workers, charging past different economics roadblocks such as 'recession', 'inflation', 'Fed rate cuts'. The Federal Reserve is shown as a train conductor, with thought bubbles contemplating next moves.\" }";
        String json = chatResponseService.getContent(id);

        String htmlContent = jsonToHtml(json);
        String key = "filename.html"; // e.g., "myfolder/myfile.html"

        PutObjectResponse response = s3Service.uploadFile(key, htmlContent);
        logger.info("html generated: "+htmlContent);
        logger.info("response: "+response);
        logger.info("response.eTag(): "+response.eTag());

        return "File uploaded with ETag: " + response.eTag();
    }


    private String jsonToHtml(String json) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><style>h1 { font-size: 24px; font-weight: bold; }</style></head><body>");

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            jsonNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                String value = field.getValue().asText();

                htmlBuilder.append("<h1>").append(capitalizeFirstLetter(key.replace("_", " "))).append("</h1>");
                htmlBuilder.append("<p>").append(value).append("</p>");
            });
        } catch (IOException e) {
            logger.error("Error parsing JSON", e);
            return "Error parsing JSON";
        }

        htmlBuilder.append("</body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.openaiResponseHtmlOutputPath + "/test123.html"))) {
            writer.write(htmlBuilder.toString());
        }catch (IOException e) {
            logger.error("Error writing HTML to file", e);
            return "Error writing HTML to file";
        }

        return htmlBuilder.toString();
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
