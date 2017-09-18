package cn.stt.netty.http.demo4;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * 初始化连接
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/12.
 */
public class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    public NettyHttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());/*HTTP 服务的解码器*/
        p.addLast(new HttpObjectAggregator(2048));/*HTTP 消息的合并处理*/
        p.addLast(new NettyHttpServerHandler()); /*自己写的服务器逻辑处理*/
    }
}
