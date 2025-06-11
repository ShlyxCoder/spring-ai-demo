package cn.org.shelly.core.model.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressFunctionRequest {
    @JsonProperty(required = true, value = "address")
    @JsonPropertyDescription("地址")
    private String address;

}