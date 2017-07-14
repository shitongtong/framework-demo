package cn.stt.dropwizard.netty.client.entry;


import cn.stt.dropwizard.netty.client.netty.NettyRestServer;

/**
 * Created by zhoumengkang on 7/1/16.
 */
public class ServerLauncher {
    public static void main(String[] args) {
        NettyRestServer nettyRestServer = new NettyRestServer();
        nettyRestServer.start();
    }
}
