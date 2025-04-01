package com.shelly.ai.config;

import com.shelly.ai.tool.ComputerService;
import com.shelly.ai.tool.TimeService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * mcp 配置
 * @author shelly
 */
@Configuration
public class McpConfig {
    @Resource
    private ComputerService computerService;

    @Resource
    private TimeService timeService;
    @Bean
    @Primary
    public ToolCallbackProvider computerTools() {
        // 适合大规模注册
        return MethodToolCallbackProvider.builder()
                .toolObjects(
                        computerService,
                        timeService
                ).build();
    }
}
