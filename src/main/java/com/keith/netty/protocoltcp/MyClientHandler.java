package com.keith.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-29
 */
public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //使用客户端发送十条数据
        for (int i = 0; i < 10; i++) {
            String mes = "hello,server ";
            byte[] content = mes.getBytes(CharsetUtil.UTF_8);
            int length = mes.getBytes(CharsetUtil.UTF_8).length;
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setLen(length);
            messageProtocol.setContent(content);
            ctx.writeAndFlush(messageProtocol);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) {
        int len = msg.getLen();
        byte[] content = msg.getContent();
        System.out.println("客户端接收到信息：长度=" + len + "内容=" + new String(content, CharsetUtil.UTF_8));
        System.out.println("客户端接收消息量=" + (++this.count));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
