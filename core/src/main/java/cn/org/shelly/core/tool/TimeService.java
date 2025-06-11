package cn.org.shelly.core.tool;

import cn.org.shelly.core.model.req.AddressFunctionRequest;
import cn.org.shelly.core.model.res.TimeFunctionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间服务
 * @author shelly
 */
@Slf4j
@Service
public class TimeService {

    @Tool(description = "获取指定地点的当前时间")
    public TimeFunctionResponse getAddressDate(AddressFunctionRequest request) {
        String result = String.format("%s的当前时间是%s",
                request.getAddress(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new TimeFunctionResponse(result);
    }

    @Tool(description = "获取当前时间")
    public TimeFunctionResponse getDate() {
        String result = String.format("当前时间是%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new TimeFunctionResponse(result);
    }
}
