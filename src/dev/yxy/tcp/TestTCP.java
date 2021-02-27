package dev.yxy.tcp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by atom on 2021/2/27
 */
public class TestTCP {

    public static void main(String[] args) throws InterruptedException, IOException {
        new Thread(() -> {
            try {
                Server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.MILLISECONDS.sleep(100);

        new Thread(() -> {
            try {
                Client.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.SECONDS.sleep(2);
        Client.stop();
        Server.stop();
    }
}
