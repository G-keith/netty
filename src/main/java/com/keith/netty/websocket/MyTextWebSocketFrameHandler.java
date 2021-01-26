package com.keith.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * TextWebSocketFrame;表示一个文本帧
 *
 * @author keith
 * @version 1.0
 * @date 2021-01-26
 */
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //id asLongText表示唯一的值
        System.out.println("建立连接成功" + ctx.channel().id().asLongText());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("服务器端收到消息：" + msg.text());
        //回复浏览器
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now() + msg.text()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("客户端断开连接" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常发生" + cause.getMessage());
        ctx.close();
    }
}
