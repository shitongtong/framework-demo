package cn.stt.pb;

import cn.stt.pb.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.net.URI;

/**
 * @ClassName PbApplicationTests
 * @Description TODO
 * @Author shitt7
 * @Date 2019/7/29 20:06
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PbApplicationTests {

    @Test
    public void test() {
        try {
            URI uri = new URI("http", null, "localhost", 30001, "/pb-api/testPost", "", null);
            HttpPost request = new HttpPost(uri);
            MessageUserLogin.MessageUserLoginRequest.Builder builder = MessageUserLogin.MessageUserLoginRequest.newBuilder();
            builder.setUsername("tom");
            builder.setPassword("123456");
            HttpResponse response = HttpUtils.doPost(request, builder.build());
//            MessageUserLogin.MessageUserLoginResponse messageUserLoginResponse = MessageUserLogin.MessageUserLoginResponse.parseFrom(response.getEntity().getContent());
            InputStream content = response.getEntity().getContent();

            MessageUserLogin.MessageUserLoginResponse messageUserLoginResponse = MessageUserLogin.MessageUserLoginResponse.parseFrom(content);
            System.out.println(messageUserLoginResponse.getAccessToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
