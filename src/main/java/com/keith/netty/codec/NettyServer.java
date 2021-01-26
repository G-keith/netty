package com.keith.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

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
             * 3.设置线程队列可连接个数
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
                            //加入编码器ProtoBufDecoder
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //指定那种对象进行解码
                            pipeline.addLast("decoder", new ProtobufDecoder(StudentPojo.Student.getDefaultInstance()));
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();

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
