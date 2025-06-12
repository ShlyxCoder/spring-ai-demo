package cn.org.shelly.server;

import cn.org.shelly.server.config.properties.BlogProperties;
import cn.org.shelly.server.gateway.BlogArticleApi;
import cn.org.shelly.server.tool.BlogTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@SpringBootApplication
@Slf4j
public class ServerApplication implements CommandLineRunner {

    @Resource
    private BlogProperties blogProperties;
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("check token ...");
        if (blogProperties.getToken() == null) {
            log.warn("token key is null, please set it in application.yml");
            System.exit(1);
        } else {
            log.info("token key is {}", blogProperties.getToken());
        }
    }

    @Bean
    public BlogArticleApi blogArticleApi() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.37.19.240:5173")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(BlogArticleApi.class);
    }

    @Bean
    public ToolCallbackProvider tools(BlogTool blogTool) {
        return MethodToolCallbackProvider.builder().toolObjects(blogTool).build();
    }
}
