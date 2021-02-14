package dev.yxy.nio.server;

/**
 * NIO服务端
 * Created by atom on 2021/2/14
 */
public class Server {
    private static final int DEFAULT_PORT = 12345;
    private static ServerHandler serverHandler;

    public static void start() {
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port) {
        if (serverHandler != null)
            serverHandler.stop();
        serverHandler = new ServerHandler(port);
        new Thread(serverHandler, "NIO-Server").start();
    }

    public static synchronized void stop() {
        if (serverHandler != null)
            serverHandler.stop();
    }
}
