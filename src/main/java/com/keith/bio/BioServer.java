package com.keith.bio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * bio服务器端
 * 1.服务器端启动一个ServerSocket
 * 2.客户端启动一个Socket对服务器进行通讯，默认情况下服务器端需要对每个客户建立一个线程与之通讯
 * 3.客户端发出请求之后，下咨询服务器是否有线程响应，如果没有则会等待或者被拒绝；如果响应，客户端线程会等待请求结束后才继续执行
 *
 * @author keith
 * @version 1.0
 * @date 2020-06-16
 */
@Slf4j
public class BioServer {
    private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().build();

    private static final ExecutorService POOL = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public void startServer() throws IOException {
        //创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(8808);
        log.debug("服务端启动成功");

        //noinspection InfiniteLoopStatement
        while (true) {
            log.debug(" 线 程 信 息 id =" + Thread.currentThread().getId() + " 名 字 =" + Thread.currentThread().getName());
            log.debug("等待连接");
            Socket socket = serverSocket.accept();
            log.debug("连接到一个客户端");
            //分配一个线程
            POOL.execute(() -> {
                //和客户端进行通信
                handler(socket);
            });
        }
    }

    /**
     * 和客户端进行通讯
     */
    public void handler(Socket socket) {
        log.debug(" 线 程 信 息 id =" + Thread.currentThread().getId() + " 名 字 =" + Thread.currentThread().getName());
        byte[] bytes = new byte[1024];
        //通过 socket 获取输入流
        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                log.debug(" 线 程 信 息 id =" + Thread.currentThread().getId() + " 名 字 =" + Thread.currentThread().getName());
                log.debug("开始读取信息");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    log.debug("客户端发送数据为{}", new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.debug("关闭连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1.创建一个线程池
     * 2.如果有客户端连接，就创建一个线程，与之通讯
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BioServer bioServer = new BioServer();
        bioServer.startServer();
    }
}
