package dev.yxy.aio.server;

import java.util.concurrent.atomic.AtomicLong;

/**
 * AIO服务端
 * Created by atom on 2021/2/14
 */
public class Server {
    private static final int DEFAULT_PORT = 12345;
    private static AsyncServerHandler serverHandler;
    public static final AtomicLong clientCount = new AtomicLong(0);

    public static void start() {
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port) {
        if (serverHandler != null)
            return;
        serverHandler = new AsyncServerHandler(port);
        new Thread(serverHandler, "AIO-Server").start();
    }

    public static synchronized void stop() {
        if (serverHandler != null) {
            serverHandler.stop();
        }
    }
}
