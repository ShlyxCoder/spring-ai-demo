package cn.org.shelly.core.config;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * ollama deepseek 配置
 * @description 这里只是用ollama的嵌入模型
 * @author shelly
 */
@Configuration
public class OllamaAIClientConfig {

//    @Bean(name = "ollamaChatClient")
//    // 注意，ollamaChat在下一个版本或许被废弃，因为其不支持tools, 这里只是做一个示例
//    public ChatClient ollamaChatClient(OllamaChatModel model) {
//        return ChatClient.builder(model)
//                .defaultOptions(OpenAiChatOptions.builder().model("deepseek-r1:1.5b").build())
//                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory())
//                )
//                .build();
//    }

    @Bean("ollamaSimpleVectorStore")
    public SimpleVectorStore vectorStore(OllamaApi ollamaApi, @Value("${spring.ai.ollama.embedding.options.model}")String model) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(model).build())
                .build();
        return SimpleVectorStore.builder(embeddingModel).build();
    }
    @Bean("ollamaPgVectorStore")
    public PgVectorStore pgVectorStore(OllamaApi ollamaApi, JdbcTemplate jdbcTemplate, @Value("${spring.ai.ollama.embedding.options.model}")String model, @Value("${spring.ai.ollama.vectorTableName}") String vectorTableName) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(model).build())
                .build();
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(vectorTableName)
                .build();
    }
}
