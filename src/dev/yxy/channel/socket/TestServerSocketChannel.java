package dev.yxy.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by atom on 2021/2/28
 */
public class TestServerSocketChannel {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(8888));
        //ssc.configureBlocking(false);
        String hello_string = "hello rudy!";
        ByteBuffer buffer = ByteBuffer.wrap(hello_string.getBytes());
        while (true) {
            System.out.println("wait for connections");
            SocketChannel clientSocket = ssc.accept();
            if (null == clientSocket) {
                //设置为非阻塞就会执行这一块代码
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(String.format("incoming connection from: %s", clientSocket.getRemoteAddress()));
                buffer.rewind();
                clientSocket.write(buffer);
                clientSocket.close();
            }
        }
    }
}
