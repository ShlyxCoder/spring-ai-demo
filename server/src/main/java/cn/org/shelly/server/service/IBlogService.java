package cn.org.shelly.server.service;

import cn.org.shelly.server.model.ArticleFunctionRequest;
import cn.org.shelly.server.model.ArticleFunctionResponse;

import java.io.IOException;

public interface IBlogService {
    ArticleFunctionResponse pubBlog(ArticleFunctionRequest request) throws IOException;
}
