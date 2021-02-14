package dev.yxy.nio.client;

/**
 * Created by atom on 2021/2/14
 */
public class Client {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 12345;
    private static ClientHandler clientHandler;

    public static void start() {
        start(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static synchronized void start(String ip, int port) {
        if (clientHandler != null)
            clientHandler.stop();
        clientHandler = new ClientHandler(ip, port);
        new Thread(clientHandler, "NIO-Client").start();
    }

    public static synchronized void stop() {
        if (clientHandler != null)
            clientHandler.stop();
    }

    //向服务器发送消息
    public static boolean sendMsg(String msg) throws Exception {
        if ("quit".equalsIgnoreCase(msg)) return false;
        clientHandler.sendMsg(msg);
        return true;
    }
}
