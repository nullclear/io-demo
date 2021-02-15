package dev.yxy.aio;

import dev.yxy.aio.client.Client;
import dev.yxy.aio.server.Server;

import java.util.Scanner;

/**
 * Created by atom on 2021/2/15
 */
public class Test {
    public static void main(String[] args) throws Exception {
        //运行服务器
        Server.start();
        //避免客户端先于服务器启动前执行代码
        Thread.sleep(100);
        //运行客户端
        Client.start();
        Scanner scanner = new Scanner(System.in);
        while (Client.sendMsg(scanner.nextLine())) ;
        //关闭服务端
        Client.stop();
        //关闭客户端
        Server.stop();
    }
}
