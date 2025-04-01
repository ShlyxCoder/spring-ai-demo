package com.shelly.ai.api;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * 对话接口
 * @author shelly
 */
public interface IChatService {
    Flux<ChatResponse> generateStreamRag(String ragTag, String message, Integer isAllow);
}
