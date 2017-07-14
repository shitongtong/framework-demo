package cn.stt.jersey.helloworld;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/25.
 */
@Path("/helloworld")
public class HelloWorldService {
    @GET
    @Path("/test")
    @Produces("text/plain")
//    @Produces("text/json")
    public String helloWorld() {
        return "OK";
    }
}
