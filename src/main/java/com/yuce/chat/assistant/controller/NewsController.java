package com.yuce.chat.assistant.controller;

import com.yuce.chat.assistant.config.AiAgent;
import com.yuce.chat.assistant.model.NewsAndSentimentals;
import com.yuce.chat.assistant.util.AgentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final ChatClient chatClient ;
    private final BeanOutputConverter<List<NewsAndSentimentals>> outputConverter;

    public NewsController(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<NewsAndSentimentals>>() {
                });
    }

    /**
     * Soru: "İlgi alanı" ikinci beane nasıl gidiyor?
     * Cevap: Bunu sen manuel olarak Java kodu içinde birbirine bağlamıyorsun.
     * Bunun yerine LLM (Language Model) yani OpenAI veya Spring AI bu zincirleme işlemi "Function Calling" üzerinden otomatik yapıyor.
     * <p>
     * Soru: İkinci Bean, birinci bean’in ürettiği ilgi alanı verisini nasıl alıyor?
     * Cevap: Bu geçiş, OpenAI'nin Function Calling özelliği sayesinde otomatik gerçekleşiyor.
     * <p>
     * LLM'nin Karar Süreci
     * LLM, bu iki fonksiyonu bir zincir şeklinde çalıştırabilir:
     * <p>
     * Prompt’ta sen userId verdin.
     * <p>
     * LLM, önce "kullanıcının ilgi alanını bulmalıyım" diye karar verir.
     * <p>
     * İlk fonksiyonu çağırır: getUserPreferences(userId: 123)
     * <p>
     * Gelen cevap "crypto" olur.
     * <p>
     * Ardından ikinci fonksiyonu çağırır: getLatestNewsByTopic(topic: "crypto")
     * <p>
     * Sonuç olarak özetlenmiş haberleri sana döner.
     * <p>
     * Bu iş akışı içinde Java'da manuel veri aktarımı yapmana gerek yok — LLM bu çağrıları kendi içinde orchestration ederek halleder.
     *
     * @param userId
     * @return
     */

    //https://dev.to/lucasnscr/ai-agent-patterns-with-spring-ai-43gl
    @GetMapping("/short")
    public List<NewsAndSentimentals> getNewsFromInterest(@RequestHeader Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        UserMessage userMessage = new UserMessage(
                """
                        Get summarize news by topic depending on userId: %s.
                        Get topic by userId first so you can request news based on user topic
                        Only result need to be provided
                        Example:
                        [
                         {
                            String title,
                            String url,
                            String timePublished,
                            String summary,
                            double overallSentimentScore,
                            String overallSentimentLabel
                         }
                        ]
                        """
                        .formatted(userId)
        );
        ChatOptions aiChatOptions = AgentUtil.createFunctionOptions(AiAgent.GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME, AiAgent.GET_USER_PREFERENCES_FUNCTION_NAME);
        ChatResponse response = this.chatClient.prompt(new Prompt(userMessage, aiChatOptions)).advisors(new SimpleLoggerAdvisor()).call().chatResponse();
        logger.info("Response: {}", response);
        Generation generation = response.getResult();
        return this.outputConverter.convert(generation.getOutput().getText());
    }
}