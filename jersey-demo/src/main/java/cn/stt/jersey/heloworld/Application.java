//package cn.stt.jersey.heloworld;
//
//import com.sun.net.httpserver.HttpServer;
//import org.glassfish.jersey.server.ResourceConfig;
//
//import java.io.IOException;
//import java.net.URI;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * @Author shitongtong
// * <p>
// * Created by shitongtong on 2017/4/25.
// */
//public class Application {
//    private static final URI BASE_URI = URI.create("http://localhost:8080/base/");
//    public static final String ROOT_PATH = "helloworld";
//    public static void main(String[] args) {
//        try {
//            System.out.println("\"Hello World\" Jersey Example App");
//
//            final ResourceConfig resourceConfig = new ResourceConfig(HelloWorldResource.class);
//            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
//            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    server.shutdownNow();
//                }
//            }));
//            server.start();
//
//            System.out.println(String.format("Application started.\nTry out %s%s\nStop the application using CTRL+C",
//                    BASE_URI, ROOT_PATH));
//            Thread.currentThread().join();
//        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
