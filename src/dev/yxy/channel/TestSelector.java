package dev.yxy.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

/**
 * FIXME 有bug 无法理解过程
 * Created by atom on 2021/2/28
 */
public class TestSelector {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress("localhost", 8888));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        while (true) {
            int readyNum = selector.select();
            if (readyNum <= 0) {
                continue;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey tempKey = it.next();
                it.remove();
                if (tempKey.isAcceptable()) {
                    ServerSocketChannel tempChannel = (ServerSocketChannel) tempKey.channel();
                    SocketChannel clientChannel = tempChannel.accept();
                    if (null != clientChannel) {
                        System.out.println("one connection:" + clientChannel.getRemoteAddress());
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    }
                }

                if (tempKey.isReadable()) {
                    SocketChannel tempChannel = (SocketChannel) tempKey.channel();
                    tempChannel.read(buffer);
                    buffer.flip();
                    decoder.decode(buffer, charBuffer, false);
                    charBuffer.flip();
                    String getData = new String(charBuffer.array(), 0, charBuffer.limit());
                    System.out.println(tempChannel.getRemoteAddress() + ":" + getData);
                    buffer.clear();
                    charBuffer.clear();
                    tempChannel.write(ByteBuffer.allocate(0));
                    if (getData.equalsIgnoreCase("exit")) {
                        tempChannel.close();
                    }
                }

                if (tempKey.isWritable()) {
                    SocketChannel tempChannel = (SocketChannel) tempKey.channel();
                    tempChannel.write(ByteBuffer.wrap("Hello".getBytes()));
                    System.out.println(tempChannel.getRemoteAddress() + ": read");
                }
            }
        }
    }
}
