package dev.yxy.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by atom on 2021/2/27
 */
public class Server {
    private static ServerSocket sever;

    public static void start() throws IOException {
        Socket client = null;
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        OutputStream out = null;
        try {
            //创建服务器套接字
            sever = new ServerSocket(8080);
            System.out.println("[服务器] - 服务器启动成功！");
            // 阻塞，等待获取客户端连接
            client = sever.accept();
            System.out.println("[服务器] - 客户端IP：" + client.getInetAddress());

            //获取客户端的消息
            in = client.getInputStream();
            baos = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            //强制刷新
            baos.flush();
            String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("[服务器] - 客户端发来消息：" + data);

            //向客户端发送消息
            out = client.getOutputStream();
            String message = "客户端你好，我是服务器！";
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
            // shutdown输出流
            client.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (baos != null) {
                baos.close();
            }
            if (in != null) {
                in.close();
            }
            if (client != null) {
                client.close();
            }
        }
    }

    public static void stop() throws IOException {
        if (sever != null) {
            sever.close();
        }
    }
}
