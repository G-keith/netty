package com.keith.nio;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 可让文件直接在内存(堆外内存)修改, 操作系统不需要拷贝一次
 * @author keith
 * @version 1.0
 * @date 2020-06-17
 */
public class TestMappedByteBuffer {

    public static void main(String[] args) throws IOException {
        File file=new File("/Users/gemi/Desktop/2.txt");
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
        FileChannel fileChannel=randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');
        mappedByteBuffer.put(4, (byte) 'Y');
    }
}
