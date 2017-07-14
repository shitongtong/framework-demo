package cn.stt.es.demo;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/12.
 */
public class Demo1 {

    private TransportClient client;

    public static void main(String[] args) throws IOException {


    }

    /**
     * 获取es客户端对象
     */
    @Before
    public void test3() throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.219"), 9300));
        System.out.println(client);
    }

    /**
     * 关闭对象连接
     */
    @After
    public void test(){
        // on shutdown
        if (client!=null){
            client.close();
        }
    }

    @Test
    public void test2() throws IOException {
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()
                )
                .get();
    }

    @Test
    public void test1() throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("user", "kimchy")
                .field("postDate", new Date())
                .field("message", "trying out Elasticsearch")
                .endObject();
        String json = builder.string();
        System.out.println(json);
    }
}
