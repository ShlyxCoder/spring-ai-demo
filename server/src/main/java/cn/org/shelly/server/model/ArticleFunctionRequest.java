package cn.org.shelly.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleFunctionRequest {

    @JsonProperty(required = true, value = "articleTitle")
    @JsonPropertyDescription("文章标题")
    private String articleTitle;

    @JsonProperty(required = true, value = "articleContent")
    @JsonPropertyDescription("文章内容")
    private String articleContent;

    @JsonProperty(required = true, value = "categoryName")
    @JsonPropertyDescription("文章分类名称")
    private String categoryName;

    @JsonProperty(required = true, value = "tagNameList")
    @JsonPropertyDescription("文章标签列表")
    private List<String> tagNameList;

    @JsonProperty(required = true, value = "articleDesc")
    @JsonPropertyDescription("文章摘要")
    private String articleDesc;


}
