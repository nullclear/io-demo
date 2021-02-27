package dev.yxy.udp;

/**
 * Created by atom on 2021/2/27
 */
public class TestUDP {

    public static void main(String[] args) {
        new Thread(Sender::create).start();
        new Thread(Sender::create).start();
        new Thread(Receiver::create).start();
        new Thread(Receiver::create).start();
    }
}
