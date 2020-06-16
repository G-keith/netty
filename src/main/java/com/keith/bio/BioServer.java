package com.keith.bio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

/**
 * bio服务器端
 *
 * @author keith
 * @version 1.0
 * @date 2020-06-16
 */
public class BioServer {
    private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().build();

    private static final ExecutorService POOL = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public void startServer() throws IOException {
        ServerSocket serverSocket=new ServerSocket(8808);
    }
}
