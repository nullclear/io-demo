package dev.yxy.nio;

import dev.yxy.nio.client.Client;
import dev.yxy.nio.server.Server;

import java.util.Scanner;

/**
 * Created by atom on 2021/2/14
 */
public class Test {
    public static void main(String[] args) throws Exception {
        //运行服务端
        Server.start();
        //避免客户端先于服务器启动前执行代码
        Thread.sleep(100);
        //运行客户端
        Client.start();
        //计算
        while (Client.sendMsg(new Scanner(System.in).nextLine())) ;
        //关闭客户端
        Client.stop();
        //关闭服务端
        Server.stop();
    }
}
