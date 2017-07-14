package cn.stt.es.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/13.
 */
public class JsonDataGeneratorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataGeneratorUtil.class);

    /**
     * 手动生成json字符串
     * @return
     */
    public static String generatorByManually(){
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        return json;
    }

    /**
     * 使用map创建json
     * @return
     */
    public static String generatorByMap(){
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("user","kimchy");
        json.put("postDate",new Date());
        json.put("message","trying out Elasticsearch");
        return json.toString();
    }

    /**
     * 序列化bean生成json
     * @param object
     * @return
     */
    public static String generatorBySerializeBean(Object object) {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        // generate json
//        byte[] json = new byte[0];
        String json = "";
        try {
//            json = mapper.writeValueAsBytes(object);
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error("类："+object+" 序列化失败，异常："+e);
        }
        return json;
    }

    /**
     * 使用ES内置的帮助类生成json
     * @return
     */
    public static XContentBuilder generatorByESHelpers(){
        XContentBuilder builder = null;
        try {
            builder = jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elasticsearch")
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
