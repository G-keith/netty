package com.keith.netty.taskqueue;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

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
        //如果这里面有耗时非常长的业务--异步执行--提交该channel对应的NIOEventLoop的taskQueue中
        //用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端2~", CharsetUtil.UTF_8));
        });
        //用户自定义定时任务,该任务是提交到scheduleTaskQueue中，和上面普通任务不在一个队列
        //任务设置的阻塞时间和上面任务执行时间是同步进行的
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(5*1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端3~", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },10, TimeUnit.SECONDS);
        System.out.println("go on ...");
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
