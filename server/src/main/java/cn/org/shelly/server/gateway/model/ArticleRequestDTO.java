package cn.org.shelly.server.gateway.model;

import cn.org.shelly.server.model.ArticleFunctionRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class ArticleRequestDTO {
    private String articleCover = "";
    private String articleTitle;
    private String articleContent;
    private String articleDesc;
    private String categoryName;
    private List<String> tagNameList;
    private Integer articleType = 1;
    private Integer isTop = 0;
    private Integer isRecommend = 0;
    private Integer status = 1;
    public static ArticleRequestDTO toDTO(ArticleFunctionRequest request){
        return new ArticleRequestDTO()
                .setArticleTitle(request.getArticleTitle())
                .setArticleContent(request.getArticleContent())
                .setArticleDesc(request.getArticleDesc())
                .setCategoryName(request.getCategoryName())
                .setTagNameList(request.getTagNameList());
    }
}
