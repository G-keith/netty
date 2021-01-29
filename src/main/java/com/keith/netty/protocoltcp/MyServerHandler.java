package com.keith.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-29
 */
public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) {
        int len = msg.getLen();
        byte[] content = msg.getContent();
        System.out.println("服务端接收到信息：长度=" + len + "内容=" + new String(content, CharsetUtil.UTF_8));
        System.out.println("服务器接收消息量=" + (++this.count));

        //回复消息
        String responseContent = UUID.randomUUID().toString();
        int responseLen = responseContent.getBytes(CharsetUtil.UTF_8).length;
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(responseLen);
        messageProtocol.setContent(responseContent.getBytes(CharsetUtil.UTF_8));
        ctx.writeAndFlush(messageProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
