package com.keith.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author keith
 * @version 1.0
 * @date 2020-06-28
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        //创建 ServerSocketChannel -
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //创建选择器
        Selector selector = Selector.open();
        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);

        //创建 ServerSocketChannel注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //这里我们等待 1 秒，如果没有事件发生, 返回
            if (selector.select(1000) == 0) {
                //没有事件发生
                System.out.println("服务器等待了 1 秒，无连接");
                continue;
            }
            ///如果返回的>0, 就获取到相关的 selectionKey 集合
            //获取SelectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //遍历 Set<SelectionKey>, 使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                //接入事件，有新的客户端连接
                if (selectionKey.isAcceptable()) {
                    //生成SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println(" 客 户 端 连 接 成 功 生 成 了 一 个 socketChannel " + socketChannel.hashCode());
                    socketChannel.configureBlocking(false);
                    //将 socketChannel 注册到 selector, 关注事件为 OP_READ， 同时给 socketChannel关联一个 Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                //可读事件处理
                if (selectionKey.isReadable()) {
                    //通过 key 反向获取到对应 SocketChannel
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //获取到该 channel 关联的 buffer
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(buffer);
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }
                //手动从集合中移动当前的 selectionKey, 防止重复操作
                keyIterator.remove();
            }
        }
    }
}
