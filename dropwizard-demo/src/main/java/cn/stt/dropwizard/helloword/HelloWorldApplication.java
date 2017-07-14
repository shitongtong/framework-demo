package cn.stt.dropwizard.helloword;

import cn.stt.dropwizard.helloword.health.TemplateHealthCheck;
import cn.stt.dropwizard.helloword.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/25.
 */
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        args = new String[]{"server","D:\\project_git\\framework-demo\\dropwizard-demo\\src\\main\\resources\\example.yml"};
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
//        return super.getName();
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
//        super.initialize(bootstrap);
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}
