package com.keith.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
public class GroupChatServer {

    private final int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    public void run() {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //获取pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //增加解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //增加编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //加入自己的业务处理类
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });
            System.out.println("netty 服务器启动完成");
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //监听关闭
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatServer(9002).run();
    }
}
