package dev.yxy.other.aio.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by atom on 2021/3/2
 */
public class AIOClientAsync {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8888), socketChannel, new CompletionHandler<>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                // 等一会
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // todo 业务逻辑
                System.out.println("客户端连接成功");
                // 异步写
                channel.write(ByteBuffer.wrap("我来自客户端...".getBytes()), channel, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel attachment) {
                        System.out.println("客户端写入大小：" + result);
                        latch.countDown();
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                        System.out.println("客户端写入失败：" + exc.getMessage());
                        latch.countDown();
                    }
                });
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
