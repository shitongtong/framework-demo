package cn.stt.jersey.helloworld;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/25.
 */
public class Application {
    public static void main(String[] args) {
        try {
            Server server = new Server(8081);

            ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
            Map<String,String> parameterMap = new HashMap<String, String>();
            //parameterMap.put("jersey.config.server.provider.classnames", "org.glassfish.jersey.server.ResourceConfig");
            parameterMap.put("jersey.config.server.provider.packages", "cn.stt.jersey");

            servletHolder.setInitParameters(parameterMap);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            context.addServlet(servletHolder, "/*");
            server.setHandler(context);

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
