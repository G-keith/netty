package com.keith.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
public class NettyByteBuf01 {

    public static void main(String[] args) {

        //创建对象，改对象包含一个数组，长度为10
        //netty的buf中读取时不需要flip转换,因为底层维护的readerIndex和writeIndex
        ByteBuf buf= Unpooled.buffer(10);

        for (int i = 0; i <10 ; i++) {
            buf.writeByte(i);
        }

        for (int i = 0; i <buf.capacity() ; i++) {
            //System.out.println(buf.getByte(i));
            System.out.println(buf.readByte() );
        }
    }
}
