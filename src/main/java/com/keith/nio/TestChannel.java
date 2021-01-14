package com.keith.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author keith
 * @version 1.0
 * @date 2020-06-17
 */
public class TestChannel {

    public static void main(String[] args) throws IOException {
       //write ();
        //read();
        //readWrite();
        transferFrom();
    }

    public static void write() throws IOException{
        String s="hello netty";

        //创建一个输出流
        FileOutputStream fileOutputStream=new FileOutputStream("/Users/gemi/Desktop/2.txt");
        //获取 对应的 FileChannel
        FileChannel fileChannel=fileOutputStream.getChannel();

        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        byteBuffer.put(s.getBytes());

        byteBuffer.flip();

        //将 byteBuffer 数据写入到 fileChannel
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }

    public static void read() throws IOException{

        File file=new File("/Users/gemi/Desktop/2.txt");
        FileInputStream fileInputStream=new FileInputStream(file);
        FileChannel fileChannel=fileInputStream.getChannel();

        ByteBuffer byteBuffer=ByteBuffer.allocate((int) file.length());

        //从通道读取数据放入缓冲区
        fileChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }

    public static void readWrite() throws IOException{
        //读
        File file=new File("/Users/gemi/Desktop/2.txt");
        FileInputStream fileInputStream=new FileInputStream(file);
        FileChannel fileChannel=fileInputStream.getChannel();
        ByteBuffer byteBuffer=ByteBuffer.allocate((int) file.length());
        fileChannel.read(byteBuffer);
        //写
        FileOutputStream fileOutputStream=new FileOutputStream("/Users/gemi/Desktop/3.txt");
        FileChannel fileChannel2=fileOutputStream.getChannel();
        byteBuffer.flip();
        //把缓冲区数据写入通道
        fileChannel2.write(byteBuffer);

        fileInputStream.close();
        fileOutputStream.close();
    }

    public static void transferFrom() throws IOException{
        //读通道
        File file=new File("/Users/gemi/Desktop/2.txt");
        FileInputStream fileInputStream=new FileInputStream(file);
        FileChannel fileChannel=fileInputStream.getChannel();
        //写通道
        FileOutputStream fileOutputStream=new FileOutputStream("/Users/gemi/Desktop/3.txt");
        FileChannel fileChannel2=fileOutputStream.getChannel();

        //transferFrom 复制通道(fileChannel)信息到fileChannel2
        //transferTo   从当前通道(fileChannel2)复制给目标通道fileChannel
        fileChannel2.transferFrom(fileChannel,0,fileChannel.size());

        fileInputStream.close();
        fileOutputStream.close();
    }
}
