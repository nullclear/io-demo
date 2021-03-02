package dev.yxy.other.aio.syn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by atom on 2021/3/2
 */
public class AIOServerRead {

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8888));
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Void attachment) {
                System.out.println("服务器收到连接");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Future<Integer> read = channel.read(buffer);
                try {
                    // 阻塞
                    System.out.println("服务器读取消息：" + new String(buffer.array(), 0, read.get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                // 关闭
                try {
                    System.out.println("服务器 → 客户端 连接关闭");
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("服务器启动失败");
                latch.countDown();
            }
        });
        System.out.println("服务器继续...");
        latch.await();
        System.out.println("服务器关闭");
        serverSocketChannel.close();
    }
}
