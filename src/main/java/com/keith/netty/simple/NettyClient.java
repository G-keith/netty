package com.keith.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-20
 */
public class NettyClient {

    public static void send(long initialDelay, long period,String content) {
        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            //创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();

            /*
             *1.设置线程组
             * 2.设置客户端通道的实现类（反射）
             * 3.设置处理器
             */
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            //启动客户端去连接服务端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //对关闭通道进行监听
            Channel channel = channelFuture.channel();
            channel.eventLoop().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
                }
            }, initialDelay, period, TimeUnit.SECONDS);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyClient.send(5,10,"hello1111111");
    }
}
