package com.keith.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-29
 */
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) {
        System.out.println("自定义编码器");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
