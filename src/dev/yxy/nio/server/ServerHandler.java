package dev.yxy.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务端处理器
 * ----
 * 创建NIO服务端的主要步骤如下：
 * 1. 打开ServerSocketChannel，监听客户端连接
 * 2. 绑定监听端口，设置连接为非阻塞模式
 * 3. 创建Reactor线程，创建多路复用器并启动线程
 * 4. 将ServerSocketChannel注册到Reactor线程中的Selector上，监听ACCEPT事件
 * 5. Selector轮询准备就绪的key
 * 6. Selector监听到新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
 * 7. 设置客户端链路为非阻塞模式
 * 8. 将新接入的客户端连接注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
 * 9. 异步读取客户端消息到缓冲区
 * 10. 对Buffer编解码，处理半包消息，将解码成功的消息封装成Task
 * 11. 将应答消息编码为Buffer，调用SocketChannel的write将消息异步发送给客户端
 * ----
 * 因为应答消息的发送，SocketChannel也是异步非阻塞的，所以不能保证一次能吧需要发送的数据发送完，此时就会出现写半包的问题。
 * 我们需要注册写操作，不断轮询Selector将没有发送完的消息发送完毕，然后通过Buffer的hasRemain()方法判断消息是否发送完成。
 * Created by atom on 2021/2/14
 */
public class ServerHandler implements Runnable {
    private Selector selector;
    private volatile boolean started;

    /**
     * 构造方法
     *
     * @param port 指定要监听的端口号
     */
    public ServerHandler(int port) {
        try {
            //创建选择器
            selector = Selector.open();
            //打开监听通道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            //如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
            serverChannel.configureBlocking(false);//开启非阻塞模式
            //绑定端口 backlog设为1024
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            //监听客户端连接请求
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            //标记服务器已开启
            started = true;
            System.out.println("服务器已启动，端口号：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        started = false;
        System.out.println("服务端关闭成功");
    }

    @Override
    public void run() {
        //循环遍历selector
        while (started) {
            try {
                //无论是否有读写事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                //阻塞,只有当至少一个注册的事件发生的时候才会继续.
//				selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        //处理发送过来的事件
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        //循环体(监听)结束后，关闭selector并且释放里面管理的资源
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //处理发送过来的事件
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //通过ServerSocketChannel的accept创建SocketChannel实例
                //完成该操作意味着完成TCP三次握手，TCP物理链路正式建立
                SocketChannel sc = ssc.accept();
                //设置为非阻塞的
                sc.configureBlocking(false);
                //注册为读
                sc.register(selector, SelectionKey.OP_READ);
            }

            //读消息
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                //创建ByteBuffer，并开辟一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取请求码流，返回读取到的字节数
                int readBytes = sc.read(buffer);
                //读取到字节，对字节进行编解码
                if (readBytes > 0) {
                    //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip();
                    //根据缓冲区可读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    //将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String expression = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("服务器收到消息：" + expression);
                    //处理数据
                    String result;
                    try {
                        result = Calculator.cal(expression).toString();
                    } catch (Exception e) {
                        result = "计算错误：" + e.getMessage();
                    }
                    //发送应答消息
                    doWrite(sc, result);
                }
                //没有读取到字节 忽略
//				else if(readBytes==0);
                //链路已经关闭，释放资源
                else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    //异步发送应答消息
    private void doWrite(SocketChannel channel, String response) throws IOException {
        //将消息编码为字节数组
        byte[] bytes = response.getBytes();
        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        //flip操作
        writeBuffer.flip();
        //发送缓冲区的字节数组
        channel.write(writeBuffer);
        //****此处不含处理“写半包”的代码
    }
}
