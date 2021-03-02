package dev.yxy.other.aio.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by atom on 2021/3/2
 */
public class AIOServerAsync {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {

        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8888));
        serverSocketChannel.accept(serverSocketChannel, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverChannel) {
                // todo 递归，继续接受其他客户端的请求
                serverChannel.accept(serverChannel, this);

                // todo 业务逻辑
                System.out.println("服务器收到连接...");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                // 异步读消息，可以通过设置timeout等一会
                channel.read(buffer, buffer, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, ByteBuffer data) {
                        if (result == -1) {
                            System.out.println("客户端没有传输数据就close了");
                        } else {
                            System.out.println("服务器读取消息：" + new String(data.array(), 0, result));
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
                    public void failed(Throwable exc, ByteBuffer data) {
                        System.out.println("服务端读取失败：" + exc.getMessage());
                        // 关闭
                        try {
                            System.out.println("服务器 → 客户端 连接关闭");
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        latch.countDown();
                    }
                });
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverChannel) {
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
