package dev.yxy.other.aio.syn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by atom on 2021/3/2
 */
public class AIOClientWrite {

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8888), socketChannel, new CompletionHandler<>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("客户端连接成功");
                Future<Integer> write = channel.write(ByteBuffer.wrap("我来自客户端...".getBytes()));
                try {
                    // 阻塞
                    System.out.println("客户端写入大小：" + write.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("客户端操作失败");
                latch.countDown();
            }
        });
        System.out.println("客户端继续...");
        latch.await();
        System.out.println("客户端 → 服务器 连接关闭");
        socketChannel.close();
    }
}
