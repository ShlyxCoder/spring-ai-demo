package cn.org.shelly.server.test;

import cn.org.shelly.server.gateway.BlogArticleApi;
import cn.org.shelly.server.gateway.model.ArticleRequestDTO;
import cn.org.shelly.server.gateway.model.ArticleResponseDTO;
import cn.org.shelly.server.service.IBlogService;
import cn.org.shelly.server.service.impl.BlogService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApiTest {
    @Autowired
    private BlogArticleApi blogArticleApi;
    @Test
    public void test_saveArticle() throws IOException {
        // 1. 构建请求对象
        ArticleRequestDTO request = new ArticleRequestDTO();
        request.setArticleTitle("测试")
                .setArticleContent("这是一个标题")
                .setArticleDesc("这是一个标题")
                .setCategoryName("Life")
                .setTagNameList(Collections.singletonList("生活"));
        log.info("请求 JSON：" + JSON.toJSONString(request));
        // 2. 调用接口
        String token = "Bearer 863-4990-b55d-5b5366";
        Call<ArticleResponseDTO> call = blogArticleApi.saveArticle(request, token);
        Response<ArticleResponseDTO> response = call.execute();
        System.out.println("\r\n测试结果" + JSON.toJSONString(response));
        if (response.body() != null) {
            log.info(response.body().toString());
        }
        // 3. 验证结果
        if (response.body() != null && response.body().getCode() == 200) {
            ArticleResponseDTO articleResponseDTO = response.body();
            log.info("发布文章成功 {}", articleResponseDTO);
        }
    }
}
