package com.keith.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-28
 */
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    /**
     *
     * decode 会根据接收的数据，被调用多次, 直到确定没有新的元素被添加到list
     * , 或者是ByteBuf 没有更多的可读字节为止
     * 如果list out 不为空，就会将list的内容传递给下一个 channelinboundhandler处理, 该处理器的方法也会被调用多次
     * @param ctx 上下文对象
     * @param in  入站的buf
     * @param out list集合，将数据传给下个Handler
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
