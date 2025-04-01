package com.shelly.ai.model.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeFunctionResponse {
    @JsonProperty(required = true, value = "date")
    @JsonPropertyDescription("时间")
    private String date;
    public TimeFunctionResponse(String date) {
        this.date = date;
    }
}