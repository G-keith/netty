package com.keith.nio;

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

        //创建一个buffer，大小为5
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向 buffer 存放数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }
        //position=5
        read(intBuffer);
        readOnly(intBuffer);
    }

    public static void read(IntBuffer intBuffer) {
        //如何从 buffer 读取数据
        // 将 buffer 转换，读写切换(!!!)
        //flip  将position置为0，从头开始读
        intBuffer.flip();
        //判断是否还有元素，position=limit
        while (intBuffer.hasRemaining()) {
            System.out.print(intBuffer.get()+" ");
        }
        System.out.println();
    }

    public static void readOnly(IntBuffer intBuffer) {
        intBuffer.flip();
        //只读buffer
        IntBuffer readOnlyBuffer = intBuffer.asReadOnlyBuffer();
        //判断是否还有元素，position=limit
        while (intBuffer.hasRemaining()) {
            System.out.print(intBuffer.get()+" ");
        }
        System.out.println();
        //readOnlyBuffer.put(11);
    }
}
