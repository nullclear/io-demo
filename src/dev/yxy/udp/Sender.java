package dev.yxy.udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by Nuclear on 2020/8/7
 */
public class Sender extends JFrame implements Runnable, ActionListener, WindowListener {
    // 广播组地址
    private static InetAddress group;

    static {
        try {
            // 地址要求:224.0.0.0~239.255.255.255
            group = InetAddress.getByName("224.255.10.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static final int port = 8088;
    // 多播数据包套接字
    private MulticastSocket socket;
    private volatile boolean flag = false;
    private Thread thread;

    // GUI
    private JButton start = new JButton("开始广播");
    private JButton pause = new JButton("暂停广播");

    public Sender() {
        super("广播发送器");
        init();
    }

    private void init() {
        // 绑定按钮start的单击事件
        start.addActionListener(this);
        // 绑定按钮pause的单击事件
        pause.addActionListener(this);
        // 指定文本域中文字颜色
        // 设置关闭操作
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        // JFrame 中心加入 JPanel
        JPanel center = new JPanel();
        // 设置面板布局 2 行 1 列
        center.setLayout(new GridLayout(2, 1));
        center.add(start);
        center.add(pause);
        add(center, BorderLayout.CENTER);

        validate();
        // 设置布局
        setBounds(10, 50 + new Random().nextInt(100), 400, 200);
        // 将窗体设置为显示状态
        setVisible(true);
    }

    // 单例创建
    private static volatile Sender sender;

    public static void create() {
        if (sender == null) {
            synchronized (Sender.class) {
                if (sender == null) {
                    sender = new Sender();
                    sender.start();
                }
            }
        }
    }

    public void start() {
        try {
            // 实例化多播套接字
            socket = new MulticastSocket(port);
            // 加入广播组
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        // 只能启动一个广播线程
        if (thread == null || (!thread.isAlive())) {
            flag = true;
            thread = new Thread(this);
        }
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("[广播] - 启动");
        while (flag) {
            String message = "[" + System.currentTimeMillis() + "] - 天气预报：当前天气晴";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);//数据包发送
            try {
                socket.send(packet);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[广播] - 暂停");
    }

    public void pause() {
        flag = false;
    }

    public void stop() {
        pause();
        socket.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 单击按钮start触发的事件
        if (e.getSource() == start) {
            start.setBackground(Color.red);
            pause.setBackground(Color.yellow);
            resume();
        }

        // 单击按钮pause触发的事件
        if (e.getSource() == pause) {
            start.setBackground(Color.yellow);
            pause.setBackground(Color.red);
            pause();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int option = JOptionPane.showConfirmDialog(this, "确定关闭发送器？", "提示", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (e.getWindow() == this) {
                stop();
                this.dispose();
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
