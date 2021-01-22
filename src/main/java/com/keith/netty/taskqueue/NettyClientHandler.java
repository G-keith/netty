package com.keith.netty.taskqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-20
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪时，就会触发该方法
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(("client：" + ctx));
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,Server ლ(′◉❥◉｀ლ)", CharsetUtil.UTF_8));
    }

    /**
     * 当通道有读取事件时会触发
     *
     * @param ctx 上下文对象
     * @param msg 消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复的消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 发送异常
     *
     * @param ctx   上下文对象
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("发送异常，通道关闭");
        cause.printStackTrace();
        ctx.close();
    }
}
