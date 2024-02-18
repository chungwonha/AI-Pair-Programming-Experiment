package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    static Logger logger = LoggerFactory.getLogger(Util.class);
    public static List<SentimentScore> convertToSentimentScoreList(List<LinkedHashMap<String, Object>> mapList) {
        return mapList.stream().map(map -> {
            SentimentScore score = new SentimentScore();

            if (map.containsKey("uniqueIdentifier")) {
                score.setUniqueIdentifier((String) map.get("uniqueIdentifier"));
            }
            if (map.containsKey("sentimentScore")) {
                score.setSentimentScore((Double) map.get("sentimentScore"));
            }
            if (map.containsKey("publishedDate")) {
                score.setPublishedDate((String) map.get("publishedDate"));
            }
            if (map.containsKey("articleTitle")) {
                score.setArticleTitle((String) map.get("articleTitle"));
            }
            if (map.containsKey("articleType")) {
                score.setArticleType((String) map.get("articleType"));
            }
            if (map.containsKey("articleSource")) {
                score.setArticleSource((String) map.get("articleSource"));
            }

            return score;
        }).collect(Collectors.toList());
    }

    /*
        * Convert the date string from the format "MMMM d, yyyy h:mm a" to "yyyy-MM-dd"
        * Example: "September 1, 2019 12:00 p.m. EDT" -> "2019-09-01"
        * This is for Wahington Post articles published date
     */
    public static String convertDate1(String originalDateString) {
        try {
            // Preprocess the string to remove "at" and convert "p.m."/"a.m." to "PM"/"AM"
            String processedString = originalDateString
                    .replace(" at ", " ")
                    .replace("p.m.", "PM")
                    .replace("a.m.", "AM")
                    .replaceAll(" [A-Z]{3}", ""); // Remove the timezone part if not needed

            // Define the original format of the date string
            DateTimeFormatter originalFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a");

            // Define the target format
            DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse and format
            LocalDateTime dateTime = LocalDateTime.parse(processedString, originalFormat);
            return dateTime.format(targetFormat);
        } catch (DateTimeParseException e) {
            logger.info("Error parsing the date: " + originalDateString  + " "+e.getMessage()+ " " + e.getStackTrace());
            return null;
        }
    }

    /*
     * Convert the date string from the format "MMM. dd, yyyy h:mm a" to "yyyy-MM-dd"
     * Example: "Sep. 01, 2019 12:00 p.m. ET" -> "2019-09-01"
     * This is for WSJ articles published date
     */
    public static String convertDate2(String originalDateString) {
        try {
            // Remove the timezone information if not needed for parsing
            String processedString = originalDateString.replaceAll(" ET", "");

            // Define the original format of the date string
            DateTimeFormatter originalFormat = DateTimeFormatter.ofPattern("MMM. dd, yyyy h:mm a");

            // Define the target format
            DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse and format
            LocalDateTime dateTime = LocalDateTime.parse(processedString, originalFormat);
            return dateTime.format(targetFormat);
        } catch (DateTimeParseException e) {
            logger.info("Error parsing the date: " + e.getMessage());
            return null;
        }
    }

    /*
        * Convert the date string from the format "MMM. dd, yyyy h:mm a" to "yyyy-MM-dd"
        * Example: "Updated Sep. 01, 2019 12:00 p.m. ET" -> "2019-09-01"

     */

        public static String convertDate3(String originalDateString) {
            try {
                Map<String, String> monthAbbreviations = new HashMap<>();

                // Add more four-letter abbreviations and their replacements if needed
                monthAbbreviations.put("Sept", "Sep");


                // Check if the string starts with "Updated" and remove it if present
                String processedString = originalDateString.startsWith("Updated")
                        ? originalDateString.replaceFirst("Updated ", "")
                        : originalDateString;

                // Replace any four-letter month abbreviations
                for (Map.Entry<String, String> entry : monthAbbreviations.entrySet()) {
                    processedString = processedString.replace(entry.getKey() + ".", entry.getValue() + ".");
                }

                // Remove the timezone information
                processedString = processedString.replaceAll(" ET", "");

                // Replace 'a.m.'/'p.m.' with 'AM'/'PM'
                processedString = processedString.replace("p.m.", "PM").replace("a.m.", "AM");

                // Define the original format of the date string
                SimpleDateFormat originalFormat = new SimpleDateFormat("MMM. dd, yyyy h:mm a");

                // Parse the date
                Date date = originalFormat.parse(processedString);

                // Define the target format
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Format the date
                return targetFormat.format(date);
            } catch (ParseException e) {
                logger.info("Error parsing the date: " + originalDateString  + " " + e.getMessage() + " " + e.getStackTrace());
                return null;
            }
        }

    public static String extractDate1(String text) {
        String regex = "\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String dateString = matcher.group();
            return dateString;
        }
        return null;
    }

    public static String extractDate2(String text) {
        // Regex to match the date pattern, allowing for characters right after 'ET'
        //String regex = "(\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET)(?![a-zA-Z])";
        String regex = "(\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String dateString = matcher.group(1); // Get the matched date string
            return dateString;
        }
        return null;
    }

//    public static String extractPublishedDateFromWsj(String text) {
//        // Regex to match the date pattern more flexibly
//        String regex = "(\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(text);
//
//        while (matcher.find()) {
//            String dateString = matcher.group(1); // Get the matched date string
//            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd, yyyy h:mm a 'ET'");
//                return dateString;//, formatter);
//            } catch (DateTimeParseException e) {
//                e.printStackTrace();
//                // If parsing fails, continue to the next match
//            }
//        }
//        return null;
//    }

    public static String extractPublishedDateFromWsj(String text) {
        // Regex to match the date pattern more flexibly
        //String regex = "(?:\\b|^)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET(?:\\b|$)";
        //String regex = "(?:[^a-zA-Z]|^)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET";
//          String regex = "(?<=\\b|[^a-zA-Z])(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET";
        String regex = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\. \\d{1,2}, \\d{4} \\d{1,2}:\\d{2} [ap]m ET";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String dateString = matcher.group().trim(); // Get the matched date string
            // Try to format the date, or return the raw date string if formatting fails
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd, yyyy h:mm a 'ET'");
                return formatter.format(formatter.parse(dateString));
            } catch (DateTimeParseException e) {
                return dateString;
            }
        }
        return null;
    }
    private static LocalDateTime parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM. dd, yyyy h:mm a 'ET'", Locale.ENGLISH);
        try {
            return LocalDateTime.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String reformatWashingtonPostDateString(String originalDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a. 'EST'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMM-dd-yyyy");
        try {
            Date date = originalFormat.parse(originalDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Or handle the error appropriately
        }
    }

    public static String removeCarriageReturns(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\r", ""); // Replace carriage returns with an empty string
    }

    public static String removeExtraSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " "); // Replace multiple spaces with a single space
    }

    public static String escapeDoubleQuotes(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\"", "\\\"");
    }

    public static String sanitizeFileName(String input) {
        if (input == null) {
            return null;
        }
        // Replace spaces with underscores
        String sanitized = input.replace(" ", "_");
        // Remove special characters except underscores and dots
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._]", "");
        return sanitized;
    }
    String s = "{\"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \"By Bob FernandezNov. 6, 2023 5:37 pm ETGift unlocked articleListen(2 min)Neel Kashkari, president of the Federal Reserve Bank of Minneapolis, said he has concerns about inflation “ticking up again. That’s what I’m worried about.” Photo: MIKE SEGAR/REUTERSA top Federal Reserve official said he would err on the side of overtightening monetary policy rather than not doing enough to bring inflation down to the central bank’s 2% target.“Undertightening will not get us back to 2% in a reasonable time,” Neel Kashkari, the president of the Federal Reserve Bank of Minneapolis, said in an interview with The Wall Street Journal on Monday.The economy has proven resilient, Kashkari said. But he has concerns about inflation “ticking up again. That’s what I’m worried about.”He said that some prices and wages data indicate that inflation could be “settling somewhere north of 2%, and that would be very concerning to me.”The Fed held interest rates steady at a policy meeting last week, and some market participants viewed comments by Fed Chair Jerome Powell as possibly indicating that the central bank may be done raising interest rates with inflation substantially down from the summer of 2022 and the job market seemingly cooling with slightly higher unemployment.Newsletter Sign-upCentral BankingCentral banking news, analysis and insights from WSJ's global team of reporters and editors.SUBSCRIBEThe Labor Department reported on Friday that the U.S. economy added 150,000 jobs in October and that the unemployment rate rose to 3.9%. The government also said the economy didn’t generate as many new jobs as previously reported in the prior two months, a signal of a softer job market that the Fed has sought. The August gain was cut to 165,000, from 227,000. A blockbuster September gain was shaved to 297,000, from 336,000.Along with the potential for higher inflation, the economy also faces threats such as geopolitical turmoil and a potential U.S. government shutdown. Additionally. long-term Treasury yields have risen, and borrowing has become more expensive for consumers and businesses.Kashkari said that he needed more information to come to a firm decision on interest-rate steps moving forward. “I am not ready to say we are in a good place,” Kashkari said.Advertisement - Scroll to Continue\n" +
            "        Kashkari has a vote on the Fed policy committee that determines interest rates. The committee meets next Dec. 12-13.Copyright ©2023 Dow Jones & Company, Inc. All Rights Reserved. 87990cbe856818d5eddac44c7b1cdeb8What to Read NextCentral BanksEven if the Fed Stays on Hold, Jerome Powell Is Keeping His Options OpenNovember 9, 2023The Fed chair said it was premature for the central bank to declare a conclusive end to its historic interest-rate increases of the past two years even though he didn’t make an argument for further hikes right now.Continue To ArticleEconomic DataCanada Unemployment Rate Continued to Inch Up in November12 hours agoCanada’s jobless rate continued to push higher last month with an increase in layoffs and as the pace of hiring was again outpaced by hot population growth.Continue To Article\n" +
            "        U.S. EconomyJerome Powell Signals Fed Will Extend Interest-Rate PauseOctober 19, 2023Recent economic figures show “ongoing progress” toward the goals of lowering inflation while sustaining strong employment, the U.S. central bank chair says.Continue To ArticleU.S. EconomyFed’s Interest Rate Hikes Are Probably Over, but Officials Are Reluctant to Say So19 hours agoThe central bank is on track to extend its rate-hike pause through January.Continue To ArticleObituariesWhat Warren Buffett Said About Charlie MungerNovember 28, 2023Here are some of the Oracle of Omaha’s comments through the years on his longtime investing partner, who died Tuesday at 99.Continue To ArticleCentral BanksInflation Fight Has Come Down to Housing, Chicago Fed’s Goolsbee Says9 hours agoInflation seems on track toward the Federal Reserve’s 2% target and now the big question is what will happen with housing in 2024, a top Fed official said Friday.Continue To ArticleU.S. stocks open lower after strong November as investors await Fed Chair Jerome Powell15 hours agoU.S. stocks opened lower on Friday, kicking off December with a slight decline, as investors await remarks from Federal Reserve Chair Jerome Powell. The Dow...Continue To ArticleSprawling Las Vegas Megamansion Built for the Prince of Brunei Sells for $25 Million8 hours agoThe nearly finished compound has 25 bedrooms and 46 bathrooms. The deal is tied for the priciest sale in the city.Continue To ArticleSponsored OffersTurboTax: \n" +
            "Save up to $15 with TurboTax coupon 2023The Motley Fool: \n" +
            "Epic Bundle - 3x Expert Stock RecommendationsH&R Block Tax: \n" +
            "Get 20% off H&R Block tax software productsTop Resume: \n" +
            "Top Resume Coupon: 10% Off professional resume writingeBay: \n" +
            "Shop Cyber Week deals on eBay - Up to 70% off todayGroupon: \n" +
            "Extra 10% off your order with Groupon discount code\"}]}";
}
