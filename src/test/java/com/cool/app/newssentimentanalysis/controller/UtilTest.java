package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.openai.ChatResponse;
import com.cool.app.newssentimentanalysis.repository.openai.ChatResponseRepository;
import com.cool.app.newssentimentanalysis.service.BloggerService;
import com.cool.app.newssentimentanalysis.service.ChatResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UtilTest {
    @Test
    void convertDate3_WithUpdated() {
        String testDate = "Updated Nov. 15, 2023 4:48 pm ET";
        String expected = "2023-11-15";
        assertEquals(expected, Util.convertDate3(testDate));
    }

    @Test
    void convertDate3_WithoutUpdated() {
        String testDate = "Nov. 15, 2023 4:48 pm ET";
        String expected = "2023-11-15";

        String testDate2 = "Sept. 20, 2023 5:08 am ET";
        String expected2 = "2023-09-20";
        assertEquals(expected2, Util.convertDate3(testDate2));
    }

    @Test
    void convertDate3_InvalidFormat() {
        String testDate = "Invalid date format";
        assertNull(Util.convertDate3(testDate));
    }

    @Test
    void testExtractDate() {
        String testText1 = "Some article text with a date Dec. 14, 2023 1:44 pm ETin it.";
        String testText2 = "Some article text with a date Dec. 14, 2023 1:44 pm ET in it.";
        String testText3 = "Some article text with a dateDec. 14, 2023 1:44 pm ET in it.";
        String testText4 = "Some article text with a dateDec. 14, 2023 1:44 pm ETin it.";

        String expectedDate = "Dec. 14, 2023 1:44 pm ET";//LocalDateTime.of(2023, Month.DECEMBER, 14, 13, 44);

        String actualDate1 = Util.extractPublishedDateFromWsj(testText1);
        String actualDate2 = Util.extractPublishedDateFromWsj(testText2);
        String actualDate3 = Util.extractPublishedDateFromWsj(testText3);
        String actualDate4 = Util.extractPublishedDateFromWsj(testText4);

        assertNotNull(actualDate1, "The date should not be null");
        assertNotNull(actualDate2, "The date should not be null");

        assertEquals(expectedDate, actualDate1, "The extracted date should match the expected date");
        assertEquals(expectedDate, actualDate2, "The extracted date should match the expected date");
        assertEquals(expectedDate, actualDate3, "The extracted date should match the expected date");
        assertEquals(expectedDate, actualDate4, "The extracted date should match the expected date");
    }

    @Test
    void testExtractDateWithNoDateInText() {
        String testText = "Some article text without a date.";

        String actualDate = Util.extractPublishedDateFromWsj(testText);

        assertNull(actualDate, "The date should be null for text without a date");
    }

    @Test
    void testReformatDateStringValid() {
        String inputDate = "November 21, 2023 at 9:00 a.m. EST";
        String expectedDate = "Nov-21-2023";

        String actualDate = Util.reformatWashingtonPostDateString(inputDate);

        //assertEquals(expectedDate, actualDate, "The reformatted date should match the expected format");
    }

    @Test
    void testReformatDateStringInvalid() {
        String inputDate = "Invalid Date String";
        String actualDate = Util.reformatWashingtonPostDateString(inputDate);

        assertNull(actualDate, "The method should return null for invalid date strings");
    }

    @Autowired
    private BloggerService bloggerService;

//    @Test
//    void testPostToBlogger() {
//        String text = "This is a test blog post.";
//        bloggerService.postToBlogger(text);
//    }

    @Test
    public void testRemoveCarriageReturns() {
        // Test with carriage returns
        String withCarriageReturns = "Hello\rWorld\r!";
        String expectedWithoutCarriageReturns = "{\n" +
                "  \"model\": \"gpt-4\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"By Bob FernandezNov. 6, 2023 5:37 pm ETGift unlocked articleListen(2 min)Neel Kashkari, president of the Federal Reserve Bank of Minneapolis, said he has concerns about inflation “ticking up again. That’s what I’m worried about.” Photo: MIKE SEGAR/REUTERSA top Federal Reserve official said he would err on the side of overtightening monetary policy rather than not doing enough to bring inflation down to the central bank’s 2% target.“Undertightening will not get us back to 2% in a reasonable time,” Neel Kashkari, the president of the Federal Reserve Bank of Minneapolis, said in an interview with The Wall Street Journal on Monday.The economy has proven resilient, Kashkari said. But he has concerns about inflation “ticking up again. That’s what I’m worried about.”He said that some prices and wages data indicate that inflation could be “settling somewhere north of 2%, and that would be very concerning to me.”The Fed held interest rates steady at a policy meeting last week, and some market participants viewed comments by Fed Chair Jerome Powell as possibly indicating that the central bank may be done raising interest rates with inflation substantially down from the summer of 2022 and the job market seemingly cooling with slightly higher unemployment.Newsletter Sign-upCentral BankingCentral banking news, analysis and insights from WSJ's global team of reporters and editors.SUBSCRIBEThe Labor Department reported on Friday that the U.S. economy added 150,000 jobs in October and that the unemployment rate rose to 3.9%. The government also said the economy didn’t generate as many new jobs as previously reported in the prior two months, a signal of a softer job market that the Fed has sought. The August gain was cut to 165,000, from 227,000. A blockbuster September gain was shaved to 297,000, from 336,000.Along with the potential for higher inflation, the economy also faces threats such as geopolitical turmoil and a potential U.S. government shutdown. Additionally. long-term Treasury yields have risen, and borrowing has become more expensive for consumers and businesses.Kashkari said that he needed more information to come to a firm decision on interest-rate steps moving forward. “I am not ready to say we are in a good place,” Kashkari said.Advertisement - Scroll to ContinueKashkari has a vote on the Fed policy committee that determines interest rates. The committee meets next Dec. 12-13.Copyright ©2023 Dow Jones & Company, Inc. All Rights Reserved. 87990cbe856818d5eddac44c7b1cdeb8What to Read NextCentral BanksEven if the Fed Stays on Hold, Jerome Powell Is Keeping His Options OpenNovember 9, 2023The Fed chair said it was premature for the central bank to declare a conclusive end to its historic interest-rate increases of the past two years even though he didn’t make an argument for further hikes right now.Continue To ArticleEconomic DataCanada Unemployment Rate Continued to Inch Up in November12 hours agoCanada’s jobless rate continued to push higher last month with an increase in layoffs and as the pace of hiring was again outpaced by hot population growth.Continue To Article  U.S. EconomyJerome Powell Signals Fed Will Extend Interest-Rate PauseOctober 19, 2023Recent economic figures show “ongoing progress” toward the goals of lowering inflation while sustaining strong employment, the U.S. central bank chair says.Continue To ArticleU.S. EconomyFed’s Interest Rate Hikes Are Probably Over, but Officials Are Reluctant to Say So19 hours agoThe central bank is on track to extend its rate-hike pause through January.Continue To ArticleObituariesWhat Warren Buffett Said About Charlie MungerNovember 28, 2023Here are some of the Oracle of Omaha’s comments through the years on his longtime investing partner, who died Tuesday at 99.Continue To ArticleCentral BanksInflation Fight Has Come Down to Housing, Chicago Fed’s Goolsbee Says9 hours agoInflation seems on track toward the Federal Reserve’s 2% target and now the big question is what will happen with housing in 2024, a top Fed official said Friday.Continue To ArticleU.S. stocks open lower after strong November as investors await Fed Chair Jerome Powell15 hours agoU.S. stocks opened lower on Friday, kicking off December with a slight decline, as investors await remarks from Federal Reserve Chair Jerome Powell. The Dow...Continue To ArticleSprawling Las Vegas Megamansion Built for the Prince of Brunei Sells for $25 Million8 hours agoThe nearly finished compound has 25 bedrooms and 46 bathrooms. The deal is tied for the priciest sale in the city.Continue To ArticleSponsored OffersTurboTax: Save up to $15 with TurboTax coupon 2023The Motley Fool: Epic Bundle - 3x Expert Stock RecommendationsH&R Block Tax: Get 20% off H&R Block tax software productsTop Resume: Top Resume Coupon: 10% Off professional resume writingeBay: Shop Cyber Week deals on eBay - Up to 70% off todayGroupon: Extra 10% off your order with Groupon discount code\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String testJson ="{\"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \"By Bob FernandezNov. 6, 2023 5:37 pm ETGift unlocked articleListen(2 min)Neel Kashkari, president of the Federal Reserve Bank of Minneapolis, said he has concerns about inflation “ticking up again. That’s what I’m worried about.” Photo: MIKE SEGAR/REUTERSA top Federal Reserve official said he would err on the side of overtightening monetary policy rather than not doing enough to bring inflation down to the central bank’s 2% target.“Undertightening will not get us back to 2% in a reasonable time,” Neel Kashkari, the president of the Federal Reserve Bank of Minneapolis, said in an interview with The Wall Street Journal on Monday.The economy has proven resilient, Kashkari said. But he has concerns about inflation “ticking up again. That’s what I’m worried about.”He said that some prices and wages data indicate that inflation could be “settling somewhere north of 2%, and that would be very concerning to me.”The Fed held interest rates steady at a policy meeting last week, and some market participants viewed comments by Fed Chair Jerome Powell as possibly indicating that the central bank may be done raising interest rates with inflation substantially down from the summer of 2022 and the job market seemingly cooling with slightly higher unemployment.Newsletter Sign-upCentral BankingCentral banking news, analysis and insights from WSJ's global team of reporters and editors.SUBSCRIBEThe Labor Department reported on Friday that the U.S. economy added 150,000 jobs in October and that the unemployment rate rose to 3.9%. The government also said the economy didn’t generate as many new jobs as previously reported in the prior two months, a signal of a softer job market that the Fed has sought. The August gain was cut to 165,000, from 227,000. A blockbuster September gain was shaved to 297,000, from 336,000.Along with the potential for higher inflation, the economy also faces threats such as geopolitical turmoil and a potential U.S. government shutdown. Additionally. long-term Treasury yields have risen, and borrowing has become more expensive for consumers and businesses.Kashkari said that he needed more information to come to a firm decision on interest-rate steps moving forward. “I am not ready to say we are in a good place,” Kashkari said.Advertisement - Scroll to Continue\n" +
                "        \n" +
                "    \n" +
                "          \n" +
                "        Kashkari has a vote on the Fed policy committee that determines interest rates. The committee meets next Dec. 12-13.Copyright ©2023 Dow Jones & Company, Inc. All Rights Reserved. 87990cbe856818d5eddac44c7b1cdeb8What to Read NextCentral BanksEven if the Fed Stays on Hold, Jerome Powell Is Keeping His Options OpenNovember 9, 2023The Fed chair said it was premature for the central bank to declare a conclusive end to its historic interest-rate increases of the past two years even though he didn’t make an argument for further hikes right now.Continue To ArticleEconomic DataCanada Unemployment Rate Continued to Inch Up in November12 hours agoCanada’s jobless rate continued to push higher last month with an increase in layoffs and as the pace of hiring was again outpaced by hot population growth.Continue To Article\n" +
                "        \n" +
                "    \n" +
                "          \n" +
                "        U.S. EconomyJerome Powell Signals Fed Will Extend Interest-Rate PauseOctober 19, 2023Recent economic figures show “ongoing progress” toward the goals of lowering inflation while sustaining strong employment, the U.S. central bank chair says.Continue To ArticleU.S. EconomyFed’s Interest Rate Hikes Are Probably Over, but Officials Are Reluctant to Say So19 hours agoThe central bank is on track to extend its rate-hike pause through January.Continue To ArticleObituariesWhat Warren Buffett Said About Charlie MungerNovember 28, 2023Here are some of the Oracle of Omaha’s comments through the years on his longtime investing partner, who died Tuesday at 99.Continue To ArticleCentral BanksInflation Fight Has Come Down to Housing, Chicago Fed’s Goolsbee Says9 hours agoInflation seems on track toward the Federal Reserve’s 2% target and now the big question is what will happen with housing in 2024, a top Fed official said Friday.Continue To ArticleU.S. stocks open lower after strong November as investors await Fed Chair Jerome Powell15 hours agoU.S. stocks opened lower on Friday, kicking off December with a slight decline, as investors await remarks from Federal Reserve Chair Jerome Powell. The Dow...Continue To ArticleSprawling Las Vegas Megamansion Built for the Prince of Brunei Sells for $25 Million8 hours agoThe nearly finished compound has 25 bedrooms and 46 bathrooms. The deal is tied for the priciest sale in the city.Continue To ArticleSponsored OffersTurboTax: \n" +
                "Save up to $15 with TurboTax coupon 2023The Motley Fool: \n" +
                "Epic Bundle - 3x Expert Stock RecommendationsH&R Block Tax: \n" +
                "Get 20% off H&R Block tax software productsTop Resume: \n" +
                "Top Resume Coupon: 10% Off professional resume writingeBay: \n" +
                "Shop Cyber Week deals on eBay - Up to 70% off todayGroupon: \n" +
                "Extra 10% off your order with Groupon discount code\"}]}";
        String ss = Util.removeExtraSpaces(testJson);
        System.out.println("ss: "+ss);

//        String sss = "{\"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \"You are a blogger to summarize an economic or finance article. You will first generate a concise, factual summary, perform sentiment analysis and score, and create witty titles for the rap section. Then, generate a list of the labels for the article with less than 255 characters separated by a comma that can be attached to the blog and can help to be found by searching more easily. Then, generate a cartoon description to depict the article so that it can be used to draw a cartoon about the article. Return the results in JSON format with the JSON keys summary, sentiment_score, rap_title,rap_lyrics, labels, and cartoon. Add a backslash to be correctly escaped in JSON. this is the article By James GlynnUpdated Nov. 6, 2023 11:36 pm ETGift unlocked articleListen(4 min)Michele Bullock, governor of the Reserve Bank of Australia, said Tuesday, ‘Inflation in Australia has passed its peak but is still too high.’ Photo: Lisa Maree Williams/Bloomberg NewsSYDNEY—The Reserve Bank of Australia raised interest rates in response to stubbornly high inflation, ending a four-month pause and diverging from other major central banks that have signaled they might have price pressures under control.The increase takes Australia’s official cash rate to 4.35%, from 4.10%, representing its highest level in more than a decade. The move was widely expected by economists after inflation in the three months through September showed prices of services, fuel, and rents climbing again.“Inflation in Australia has passed its peak but is still too high and is proving more persistent than expected a few months ago,” Governor Michele Bullock said on Tuesday after chairing her second meeting.Australia’s economic recovery from the Covid-19 pandemic has been supported by a resilient consumer and record migration that has eased labor shortages and boosted house prices. But those forces are also keeping inflation elevated by strengthening demand for services, including education, and intensifying competition for places to live.Consumer prices in Australia in the three months through September rose by 5.4% over the year prior. That is almost double the annualized rate in the U.S. for the same period. The inflation rate is also significantly higher than in the eurozone.The different degrees of success that central banks have achieved in sapping price pressures after lifting interest rates at an unprecedented clip explain why officials around the world are taking an increasingly nuanced approach. Many central banks are hinting that they have done enough, while keeping the door open to tightening policy if the data again flash red.The Federal Reserve left interest rates on hold for a second consecutive policy meeting last week, saying the U.S. economy has yet to feel the full effects of prior increases. In late October, the European Central Bank left interest rates unchanged, ending a streak of 10 consecutive rate hikes.Australia also thought it was done with rates—twice. The RBA first hit pause in April, ahead of the Fed, only to resume with two more hikes. It had then been on hold since July.Advertisement - Scroll to Continue The RBA will publish new economic forecasts on Friday that economists expect to show inflation returning to its 2%-to-3% target slowly over the next two years, while the economy loses momentum and unemployment nudges higher.Many companies say they have no choice but to keep raising prices to protect profits right now. Qantas, Australia’s largest airline, last month increased fares by an average of 3.5% to partly offset the impact of high fuel prices and a weaker Australian dollar.The rise in interest rates will be the first major test of Bullock’s leadership, and could become a flashpoint with Australia’s center-left government, which appointed her. Treasurer Jim Chalmers, while consistently stating that the central bank makes its decisions independently, signaled last week that the government’s own inflation forecasts suggest the inflation outlook hasn’t deteriorated materially.Bullock took the helm of the RBA in September after the departure of Philip Lowe as governor. Lowe’s exit was marked by controversy after a public backlash that followed a record rise in interest rates that began in early 2022.“Whether further tightening of monetary policy is required to ensure that inflation returns to target in a reasonable time frame will depend upon the data and the evolving assessment of risks,” Bullock said on Tuesday. She cited China’s economic health and the implications of conflicts overseas as potential threats to Australia’s outlook.Corrections & Amplifications The Reserve Bank of Australia raised interest rates in response to stubbornly high inflation, ending a four-month pause. An earlier version of the article said the bank had paused for five months. (Nov. 7)Write to James Glynn at james.glynn@wsj.comCopyright ©2023 Dow Jones & Company, Inc. All Rights Reserved. 87990cbe856818d5eddac44c7b1cdeb8What to Read NextU.S. EconomyFed’s Interest Rate Hikes Are Probably Over, but Officials Are Reluctant to Say So19 hours agoThe central bank is on track to extend its rate-hike pause through January.Continue To ArticleCentral BanksReserve Bank of Australia Warns of Low Tolerance for Stubborn InflationOctober 16, 2023Minutes of the October board meeting showed RBA board members mulled raising the cash rate by 25 basis points amid worries that persistently high gasoline prices could slow progress in lowering inflation.Continue To Article EuropeBritain’s Royal Family Is Again Embroiled In Allegation of Racism6 hours agoA now-retracted book named family members who discussed the skin color of Meghan Markle and Prince Harry’s son.Continue To ArticleCentral BanksAustralia’s Chalmers Appoints BOE Official as RBA Deputy GovernorNovember 26, 2023Australian Treasurer Jim Chalmers has moved again to shake up the corporate culture and practices of the Reserve Bank of Australia, naming a Bank of England official as the central bank’s new deputy governor.Continue To ArticleCentral BanksInflation Fight Has Come Down to Housing, Chicago Fed’s Goolsbee Says9 hours agoInflation seems on track toward the Federal Reserve’s 2% target and now the big question is what will happen with housing in 2024, a top Fed official said Friday.Continue To ArticleHeard on the StreetThe World’s Key Canal Is Clogged Up. Winter Fuel Prices Could Get Wacky.19 hours agoClimate change could make your winter heating bills more unpredictable, particularly if you live at the end of long and vulnerable fuel supply chains.Continue To ArticleFed Chairman Jerome Powell: 'Premature' to speculate on interest-rate cuts12 hours agoLower inflation readings \"are welcome,\" Jerome Powell says, but \"that progress must continue\" to reach the Fed's 2% objective.Continue To ArticleThree Sydney Suburbs Where Buyers Can Find Luxury Deals This SeasonSeptember 22, 2023Close to the beach or right on it, these undervalued suburbs have more room to grow despite the city’s strong price inflationContinue To ArticleSponsored OffersTurboTax: Save up to $15 with TurboTax coupon 2023The Motley Fool: Epic Bundle - 3x Expert Stock RecommendationsH&R Block Tax: Get 20% off H&R Block tax software productsTop Resume: Top Resume Coupon: 10% Off professional resume writingeBay: Shop Cyber Week deals on eBay - Up to 70% off todayGroupon: Cyber Week! Extra 25% off your entire order with Groupon coupon\"}]}";
//        String escapedJson =Util.removeExtraSpaces(Util.escapeDoubleQuotes(sss));
//        System.out.println("escapedJson: "+escapedJson);
//        String cleanedJson = Util.removeCarriageReturns(testJson);
//System.out.println("cleanedJson: "+cleanedJson);
//        assertEquals(expectedWithoutCarriageReturns, cleanedJson);

        // Test without carriage returns
        String withoutCarriageReturns = "Hello World!";
        assertEquals(withoutCarriageReturns, Util.removeCarriageReturns(withoutCarriageReturns));

        // Test with null input
        assertNull(Util.removeCarriageReturns(null));
    }


    @Mock
    private ChatResponseRepository chatResponseRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChatResponseService chatResponseService;

    @Test
    @Transactional
    public void testSaveChatResponse() throws Exception {
        String jsonResponse = "{\n" +
                "  \"summary\": \"Despite the Federal Reserve raising interest rates to combat inflation, economic evidence suggests the central bank can't take much credit for the recent dip in US inflation. The principal drivers for the drop in inflation were factors beyond the Fed's control, primarily supply side improvements, as the economy returned to a state of normality following the global pandemic. Economists argue the Fed's role was mostly in preventing things from worsening, instead of directly contributing to the inflation drop.\",\n" +
                "  \"sentiment_score\": \"-1\",\n" +
                "  \"rap_title\": \"\\\"Can't Take Credit: Fed's Inflation Redirection\\\"\",\n" +
                "  \"rap_lyrics\": \"\\\"Yo, it's a misconception, Inflation direction, Fed took action, but got no connection. Supply side improvements, the real reflection, Fed ain't the hero, just prevention of infection.\\\"\",\n" +
                "  \"labels\": \"Federal Reserve, Inflation, Economic Analysis, Interest Rates, Supply Improvement, Pandemic Recovery\",\n" +
                "  \"cartoon\": \"A cartoon of a confused Federal Reserve represented by a man in a suit trying to take a bow on a stage, while in the background: workers, ships, and trucks (demonstrating supply improvements) receive applause from the audience. An 'Inflation' balloon slowly deflates beside him.\"\n" +
                "}"; // Your JSON response
        String uniqueIdentifier = "https://www.wsj.com/economy/central-banking/why-the-fed-shouldnt-get-credit-for-the-fall-in-inflation-576afd2b?mod=central-banking_more_article_pos13";

        // Mocking JSON parsing
        ChatResponse mockedChatResponse = new ChatResponse();
        when(objectMapper.readValue(jsonResponse, ChatResponse.class)).thenReturn(mockedChatResponse);

        // Call the method
        chatResponseService.saveChatResponse(jsonResponse, uniqueIdentifier);

        // Assertions and verifications
//        assertEquals(uniqueIdentifier, mockedChatResponse.getUniqueIdentifier());
//        verify(chatResponseRepository).save(mockedChatResponse);
        // Add more assertions and verifications as needed
    }


}
