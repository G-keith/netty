package com.keith.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-22
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        //加入netty提供的httpServerCodec 编解码器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        //增加自定义处理器
        pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());

    }
}
