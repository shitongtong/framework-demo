package cn.stt.pb.controller;

import cn.stt.pb.MessageUserLogin;
import cn.stt.pb.MessageUserLogin.MessageUserLoginResponse;
import cn.stt.pb.request.TestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author wangjinliang on 2018/10/18.
 */
@Controller
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/testPost", produces = "application/x-protobuf")
    @ResponseBody
    public MessageUserLoginResponse getPersonProto(@RequestBody MessageUserLogin.MessageUserLoginRequest request) {
        LOGGER.info("接收到参数: username={},password={}", request.getUsername(), request.getPassword());
        MessageUserLoginResponse.Builder builder = MessageUserLoginResponse.newBuilder();
        builder.setAccessToken(UUID.randomUUID().toString());
        builder.setUsername(request.getUsername());
        MessageUserLoginResponse response = builder.build();
        LOGGER.info("返回数据: username={},accessToken={}", response.getUsername(), response.getAccessToken());
        return response;
    }

    @GetMapping(value = "/testGet")
    @ResponseBody
    public MessageUserLoginResponse testGet() {
        MessageUserLoginResponse.Builder builder = MessageUserLoginResponse.newBuilder();
        builder.setAccessToken(UUID.randomUUID().toString());
        builder.setUsername("hehe");
        MessageUserLoginResponse response = builder.build();
        LOGGER.info("返回数据: username={},accessToken={}", response.getUsername(), response.getAccessToken());
        return response;
    }

    @PostMapping(value = "/testJsonPost", produces = "application/json")
    @ResponseBody
    public Map<String, String> testJsonPost(@RequestBody TestRequest request) {
        LOGGER.info("接收到参数: username={},password={}", request.getUsername(), request.getPassword());
        String uuid = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("username", request.getUsername());
        map.put("accessToken", uuid);
        LOGGER.info("返回数据: username={},accessToken={}", request.getUsername(), uuid);
        return map;
    }
}
