package dev.yxy.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 为了改进这种一连接一线程的模型，我们可以使用线程池来管理这些线程，
 * 实现1个或多个线程处理N个客户端的模型（但是底层还是使用的同步阻塞I/O），通常被称为“伪异步I/O模型“。
 * ----
 * 我们知道，如果使用CachedThreadPool线程池（不限制线程数量，如果不清楚请参考文首提供的文章），其实除了能自动帮我们管理线程（复用），
 * 看起来也就像是1:1的客户端：线程数模型，而使用FixedThreadPool我们就有效的控制了线程的最大数量，保证了系统有限的资源的控制，实现了N:M的伪异步I/O模型。
 * 但是，正因为限制了线程数量，如果发生大量并发请求，超过最大数量的线程就只能等待，直到线程池中的有空闲的线程可以被复用。
 * 而对Socket的输入流就行读取时，会一直阻塞，直到发生：
 * 1. 有数据可读
 * 2. 可用数据以及读取完毕
 * 3. 发生空指针或I/O异常
 * 所以在读取数据较慢时（比如数据量大、网络传输慢等），大量并发的情况下，其他接入的消息，只能一直等待，这就是最大的弊端。
 * Created by atom on 2021/2/13
 */
public class ServerBetter {
    //默认的端口号
    private static final int DEFAULT_PORT = 12345;
    //单例的ServerSocket
    private static ServerSocket server;
    //线程池 懒汉式的单例
    private static ExecutorService executorService = Executors.newFixedThreadPool(60);

    //根据传入参数设置监听端口，如果没有参数调用以下方法并使用默认值
    public static void start() throws IOException {
        //使用默认值
        start(DEFAULT_PORT);
    }

    //这个方法不会被大量并发访问，不太需要考虑效率，直接进行方法同步就行了
    public synchronized static void start(int port) throws IOException {
        if (server != null) return;
        try {
            //通过构造函数创建ServerSocket
            //如果端口合法且空闲，服务端就监听成功
            server = new ServerSocket(port);
            System.out.println("服务器已启动，端口号：" + port);
            //通过无线循环监听客户端连接
            //如果没有客户端接入，将阻塞在accept操作上。
            while (true) {
                Socket socket = server.accept();
                //当有新的客户端接入时，会执行下面的代码
                //然后创建一个新的线程处理这条Socket链路
                executorService.execute(new ServerHandler(socket));
            }
        } finally {
            //一些必要的清理工作
            if (server != null) {
                System.out.println("服务器已关闭。");
                server.close();
                server = null;
            }
        }
    }
}
