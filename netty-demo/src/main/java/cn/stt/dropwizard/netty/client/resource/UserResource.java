package cn.stt.dropwizard.netty.client.resource;


import cn.stt.dropwizard.netty.client.ApiProtocol;
import cn.stt.dropwizard.netty.client.BaseResource;
import cn.stt.dropwizard.netty.client.response.Info;
import cn.stt.dropwizard.netty.client.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserResource extends BaseResource {

    private final Logger logger = LoggerFactory.getLogger(UserResource.class);

//    @Resource(name = "redisService")
//    private RedisService redisService;

    public UserResource(ApiProtocol apiProtocol) {
        super(apiProtocol);
    }

    public Result get() {

        int uid;

        Object uidCheck = parameterIntCheck(apiProtocol, "uid");
        if (uidCheck instanceof Result) {
            return (Result) uidCheck;
        } else {
            uid = (int) uidCheck;
        }
        /*if (redisService == null){
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-consumer.xml"});
            redisService = context.getBean(RedisService.class);
        }*/


        String status = redisService.set("hahahahha", "jahhahah");
        logger.info("uid={},status={}",uid,status);
        return new Result<Info>(null);
    }

    public Result post() {

        return new Result<>(null);
    }

    public Result patch() {
        return success(202);
    }

    public Result delete() {
        return success(203);
    }

}
