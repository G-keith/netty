package com.keith.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
        //读取客户端发送的StudentPojo.Student
        StudentPojo.Student student = (StudentPojo.Student) msg;
        System.out.println("客户端发送的数据 id=" + student.getId() + " name=" + student.getName());
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
