package cn.org.shelly.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleFunctionResponse {

    @JsonProperty(required = true, value = "code")
    @JsonPropertyDescription("code")
    private Integer code;

    @JsonProperty(required = true, value = "msg")
    @JsonPropertyDescription("msg")
    private String msg;

    @JsonProperty(required = true, value = "url")
    @JsonPropertyDescription("url")
    private String url;

    @JsonProperty(required = true, value = "flag")
    @JsonPropertyDescription("flag")
    private boolean flag;

}
