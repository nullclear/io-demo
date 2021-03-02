package dev.yxy.other.aio.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by atom on 2021/3/2
 */
public class AIOServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8888));
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                System.out.println("服务器收到连接");
                latch.countDown();
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("服务器操作失败");
                latch.countDown();
            }
        });
        System.out.println("服务器继续...");
        latch.await();
    }
}
