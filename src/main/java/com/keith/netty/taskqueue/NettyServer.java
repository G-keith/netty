package com.keith.netty.taskqueue;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-20
 */
public class NettyServer {

    public static void start() {
        /*
         * 1.创建两个线程组BossGroup和WorkGroup
         * 2.bossGroup只处理连接请求，真正的和客户端业务处理，交给workGroup完成
         * 3.两个都是无限循环
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            /*
             *1.设置线程组
             * 2.使用NioSocketChannel作为服务器的通道实现
             * 3.设置线程队列得到连接个数
             * 4.设置保持活动连接状态
             * 5.给workGroup的EventLoop对应的管道设置处理器
             *  5.1创建一个通道初始化对象
             *  5.2给pipeline设置处理器
             */
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            //可以使用集合管理socketChannel，推送消息时，将业务加入各个channel对应的NIOEventLoop的taskQueue或者scheduleTaskQueue
                            System.out.println("客户端socketChannel hashcode=" + socketChannel.hashCode());
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            //给channelFuture注册监听器，监控我们关心的事件
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if(channelFuture1.isSuccess()){
                    System.out.println("监听端口 6668 成功");
                }else {
                    System.out.println("监听端口 6668 失败");
                }
            });

            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer.start();
    }
}
