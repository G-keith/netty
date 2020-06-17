package com.keith.nio;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * buffer 简单案例
 *
 * @author keith
 * @version 1.0
 * @date 2020-06-17
 */
public class TestBuffer {
    public static void main(String[] args) {

        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向 buffer 存放数据
        intBuffer.put(10);
        intBuffer.put(11);
        intBuffer.put(12);
        intBuffer.put(13);
        intBuffer.put(14);

        //如何从 buffer 读取数据
        // 将 buffer 转换，读写切换(!!!)
        intBuffer.flip();

        //判断是否还有元素
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
