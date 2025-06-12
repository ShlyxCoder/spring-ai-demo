package cn.org.shelly.server.service.impl;

import cn.org.shelly.server.config.properties.BlogProperties;
import cn.org.shelly.server.gateway.BlogArticleApi;
import cn.org.shelly.server.gateway.model.ArticleRequestDTO;
import cn.org.shelly.server.gateway.model.ArticleResponseDTO;
import cn.org.shelly.server.model.ArticleFunctionRequest;
import cn.org.shelly.server.model.ArticleFunctionResponse;
import cn.org.shelly.server.service.IBlogService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Service
@Slf4j
public class BlogService implements IBlogService {
    @Resource
    private BlogArticleApi blogArticleApi;

    @Resource
    private BlogProperties blogProperties;
    @Override
    public ArticleFunctionResponse pubBlog(ArticleFunctionRequest request) throws IOException {
        ArticleRequestDTO articleRequestDTO = ArticleRequestDTO.toDTO(request);
        Call<ArticleResponseDTO> call = blogArticleApi.saveArticle(articleRequestDTO, "Bearer " + blogProperties.getToken());
        Response<ArticleResponseDTO> response = call.execute();
        log.info("请求发帖 \nreq:{} \nres:{}", JSON.toJSONString(articleRequestDTO), JSON.toJSONString(response));
        if (response.body() != null && response.body().getCode() == 200) {
            ArticleResponseDTO articleResponseDTO = response.body();
            ArticleFunctionResponse articleFunctionResponse = new ArticleFunctionResponse();
            articleFunctionResponse.setCode(articleResponseDTO.getCode());
            articleFunctionResponse.setMsg(articleResponseDTO.getMsg());
            articleFunctionResponse.setUrl(blogProperties.getUrl());
            return articleFunctionResponse;
        }
        return null;
    }
}
