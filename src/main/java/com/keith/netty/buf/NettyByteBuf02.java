package com.keith.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
public class NettyByteBuf02 {

    public static void main(String[] args) {

        //创建buf对象
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello,world!", CharsetUtil.UTF_8);
        //使用相关的方法

        //buf里面有没有数组
        if(byteBuf.hasArray()){
            byte[] array = byteBuf.array();
            System.out.println(new String(array,CharsetUtil.UTF_8));

            System.out.println("byteBuf="+byteBuf);

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());
            //可读字节数
            System.out.println(byteBuf.readableBytes());

            //读取其中某一段 从几开始，读几个字节
            System.out.println(byteBuf.getCharSequence(0,4,CharsetUtil.UTF_8));
        }
    }
}
