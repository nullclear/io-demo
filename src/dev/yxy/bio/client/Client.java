package dev.yxy.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by atom on 2021/2/13
 */
public class Client {
    //默认的端口号
    private static final int DEFAULT_SERVER_PORT = 12345;
    private static final String DEFAULT_SERVER_IP = "127.0.0.1";

    public static void send(String expression) {
        send(DEFAULT_SERVER_PORT, expression);
    }

    public static void send(int port, String expression) {
        System.out.println("算术表达式为：" + expression);
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            socket = new Socket(DEFAULT_SERVER_IP, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(expression);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("___结果为：" + in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //一下必要的清理工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
