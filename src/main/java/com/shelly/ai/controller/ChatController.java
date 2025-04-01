package com.shelly.ai.controller;

import com.shelly.ai.api.IChatService;
import com.shelly.ai.common.Result;
import com.shelly.ai.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对话相关模块
 * @author shelly
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@Tag(name = "Chat模块")
public class ChatController implements IChatService {
    @Resource
    private PgVectorStore pgVectorStore;
    @Resource
    private ChatClient chatClient;

    @GetMapping
    @Operation(summary = "对话")
    @Override
    public Result<String> chat(@RequestParam String message,
                               @RequestParam(defaultValue = "你是一个助手，请用中文回答问题", required = false) String prompt){
        return Result.success(chatClient.prompt(
                // 注意，有添加次序之分，如果先添加用户的信息，将会报错
                new Prompt(List.of(new SystemMessage(prompt),new UserMessage(message)))
        ).call().content());

    }
    @GetMapping("/stream")
    @Operation(summary = "流式对话")
    @Override
    public Flux<ChatResponse> chatStream(
            @RequestParam String message,
            @RequestParam(defaultValue = "你是一个助手，请用中文回答问题", required = false) String prompt
    ) {
        return chatClient.prompt(
                new Prompt(List.of( new SystemMessage(prompt),new UserMessage(message)))
        ).stream().chatResponse();
    }
    @GetMapping("/withRag")
    @Override
    @Operation(summary = " rag对话")
    public Flux<ChatResponse> generateStreamRag(String ragTag, String message,
                              @Schema(description = "是否允许用模型自身信息库") @RequestParam(defaultValue = "1", required = false) Integer isAllow){
        log.info("ragTag:{}, message:{}", ragTag, message);
        if(StringUtils.isBlank(message) || StringUtils.isBlank(ragTag)){
            throw new CustomException("信息不能为空");
        }
        var systemPrompt = getString(isAllow);
        log.info("systemPrompt:{}", systemPrompt);
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();
        List<Document> documents = pgVectorStore.similaritySearch(request);
        List<Message> messages = new ArrayList<>();
        String documentsCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(systemPrompt).createMessage(Map.of("documents", documentsCollectors));
        messages.add(ragMessage);
        messages.add(new UserMessage(message));
        return chatClient.prompt(new Prompt(messages)).stream().chatResponse();
    }

    private static String getString(Integer isAllow) {
        String systemPrompt = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know. Please answer according to the information in the document, do not guess.
                Note that if the document I provided does not mention it, even if you know, you should pretend not to know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;
        if(isAllow == 1){
            systemPrompt += "Now you don't have to worry about the previous constraints. You can obtain information from anywhere, not just from documents";
        }
        return systemPrompt;
    }
}
