package dev.yxy.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by atom on 2021/2/28
 */
public class TestSocketChannel {

    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        boolean b = channel.connect(new InetSocketAddress("localhost", 8888));
        System.out.println(b);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        CharBuffer charBuffer = CharBuffer.allocate(100);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        channel.read(buffer);
        buffer.flip();
        decoder.decode(buffer, charBuffer, false);
        charBuffer.flip();
        while (charBuffer.hasRemaining()) {
            System.out.println(charBuffer.get());
        }
        channel.close();
    }
}
