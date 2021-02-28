package dev.yxy.channel.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by atom on 2021/2/28
 */
public class UDPClient {

    public static void main(String[] args) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        String sendData = "哈哈 hello rudy!";
        ByteBuffer buffer = ByteBuffer.wrap(sendData.getBytes());
        channel.send(buffer, new InetSocketAddress("localhost", 8888));
        System.out.println("send end!");
    }
}
