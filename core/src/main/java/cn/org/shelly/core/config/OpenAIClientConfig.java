package cn.org.shelly.core.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openai-deepseek 配置
 * @description 这里只是用openai-deepseek的聊天模型
 * @author shelly
 */
@Configuration
public class OpenAIClientConfig {

    //------------------------------------基础配置-------------------------------------------------------
    @Bean(name = "chatClient")
    public ChatClient chatClient(OpenAiChatModel model, ToolCallbackProvider toolCallbackProvider, @Value("${spring.ai.openai.chat.options.model}")String modelName) {
        return ChatClient.builder(model)
                .defaultOptions(OpenAiChatOptions.builder().model(modelName).build())
                .defaultTools(toolCallbackProvider.getToolCallbacks())
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

//    @Bean
//    public OpenAiApi openAiApi(@Value("${spring.ai.openai.base-url}") String baseUrl, @Value("${spring.ai.openai.api-key}") String apikey) {
//        return OpenAiApi.builder()
//                .baseUrl(baseUrl)
//                .apiKey(apikey)
//                .build();
//    }
    //------------------------------------rag相关配置-------------------------------------------------------
//    @Bean
//    public SimpleVectorStore vectorStore(OpenAiApi openAiApi) {
//        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi);
//        return SimpleVectorStore.builder(embeddingModel).build();
//
//    }
//
//    @Bean
//    public PgVectorStore pgVectorStore(OpenAiApi openAiApi, JdbcTemplate jdbcTemplate) {
//        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi);
//        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
//                .vectorTableName("vector_store_openai")
//                .indexType(PgVectorStore.PgIndexType.HNSW)
//                .build();
//    }

    /**
     * token切割器
     * @return TokenTextSplitter
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }
}
