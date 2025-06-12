package cn.org.shelly.server.gateway.model;

import lombok.Data;

@Data
public class ArticleResponseDTO {

    private Integer code;

    private String msg;

    private Object data;

    private boolean flag;
}
