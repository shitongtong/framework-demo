package cn.stt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = Application.class)
public class ElasticSearchTest {

    @Test
    public void test1(){
        System.out.println("111");
    }
}
