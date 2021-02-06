package com.keith.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
public class GatewayServer {

    private int port;

    public GatewayServer(int port) {
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
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new GatewayServerInitializer());
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
}
