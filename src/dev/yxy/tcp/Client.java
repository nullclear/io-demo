package dev.yxy.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by atom on 2021/2/27
 */
public class Client {
    private static Socket client;

    public static void start() throws IOException {
        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            //连接服务器
            client = new Socket("127.0.0.1", 8080);
            if (client.isConnected()) {
                System.out.println("[客户端] - 客户端连接成功！");
            }

            //向服务器发送消息
            out = client.getOutputStream();
            String message = "服务器你好，我是客户端！";
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
            // shutdown客户端的输出流
            client.shutdownOutput();

            //获取服务器的消息
            in = client.getInputStream();
            baos = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            baos.flush();
            String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("[客户端] - 服务端发来消息：" + data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                baos.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void stop() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}
