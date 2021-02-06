package com.keith.demo.netty;

import com.keith.netty.tcp.MyClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * netty服务初始化类
 *
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
public class GatewayServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new GatewayServerHandler());
    }
}
