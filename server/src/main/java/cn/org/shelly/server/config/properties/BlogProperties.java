package cn.org.shelly.server.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "blog.api")
@Component
@Data
public class BlogProperties {
    private String token;
    private String url;
}
