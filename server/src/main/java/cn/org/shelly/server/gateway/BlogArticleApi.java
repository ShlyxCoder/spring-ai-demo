package cn.org.shelly.server.gateway;

import cn.org.shelly.server.gateway.model.ArticleRequestDTO;
import cn.org.shelly.server.gateway.model.ArticleResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
public interface BlogArticleApi {
    @POST("/api/admin/article/add")
    Call<ArticleResponseDTO> saveArticle(@Body ArticleRequestDTO request, @Header("Authorization") String authorization);
}
