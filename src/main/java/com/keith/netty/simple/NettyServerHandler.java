package com.keith.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义一个Handler集成Netty规定好的HandlerAdapter（适配器）
 * 这样才能称为一个Handler
 *
 * @author keith
 * @version 1.0
 * @date 2021-01-20
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx 上下文对象，含有管道pipeline，通道channel，地址
     * @param msg 客户端发送的数据，默认object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("服务器线程信息 "+Thread.currentThread().getName());
        System.out.println("Server ctx：" + ctx);
        //将msg转成一个ByteBuf,是Netty提供的，不是NIO提供的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址是：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //将数据写入缓存并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端~", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常，关闭通道
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
