package cn.org.shelly.server.tool;

import cn.org.shelly.server.model.ArticleFunctionRequest;
import cn.org.shelly.server.model.ArticleFunctionResponse;
import cn.org.shelly.server.service.IBlogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class BlogTool {
    @Resource
    private IBlogService blogService;
    @Tool(description = "发帖到个人博客")
    public ArticleFunctionResponse queryBlog(ArticleFunctionRequest request) throws IOException {
        log.info("queryBlog: {}", request);
        return blogService.pubBlog(request);
    }
}
