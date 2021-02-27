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
public class Receiver extends JFrame implements Runnable, ActionListener, WindowListener {
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
    private MulticastSocket socket = null;
    private volatile boolean flag = true;
    private Thread thread;

    // GUI
    private JButton start = new JButton("开始接收");
    private JButton pause = new JButton("暂停接收");
    // 最新消息
    private JTextArea newestMsg = new JTextArea(10, 20);
    // 所有消息
    private JTextArea displayBoard = new JTextArea(10, 20);

    public Receiver() { // 构造方法
        super("广播接收器"); // 调用父类方法
        init();
    }

    private void init() {
        // 绑定按钮start的单击事件
        start.addActionListener(this);
        // 绑定按钮pause的单击事件
        pause.addActionListener(this);
        // 指定文本域中文字颜色
        newestMsg.setForeground(Color.blue);
        // 设置关闭操作
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        // JFrame 北部加入 JPanel
        JPanel north = new JPanel();
        north.add(start);
        north.add(pause);
        add(north, BorderLayout.NORTH);

        // JFrame 中心加入 JPanel
        JPanel center = new JPanel();
        // 设置面板布局 1 行 2 列
        center.setLayout(new GridLayout(1, 2));
        center.add(newestMsg);
        center.add(new JScrollPane(displayBoard));
        add(center, BorderLayout.CENTER);

        validate();
        // 设置布局
        setBounds(600, 50 + (new Random().nextInt(500)), 700, 400);
        // 将窗体设置为显示状态
        setVisible(true);
    }

    public static void create() {
        new Receiver().start();
    }

    private void start() {
        try {
            // 绑定多点广播套接字
            socket = new MulticastSocket(port);
            // 加入广播组
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resume() {
        flag = true;
        // 只能启动一个广播线程
        if (thread == null || (!thread.isAlive())) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        System.out.println("[接收器] - 开始接收广播");
        while (flag) {
            byte[] data = new byte[1024]; // 创建byte数组
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                // 没有取到数据包将一直阻塞
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                newestMsg.setText("正在接收的内容：\n" + message);
                displayBoard.append(message + "\n");
            } catch (IOException e) {
                //noinspection UnnecessaryContinue
                continue;
            }
        }
        System.out.println("[接收器] - 暂停接收广播");
    }

    private void pause() {
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
        int option = JOptionPane.showConfirmDialog(this, "确定关闭接收器？", "提示", JOptionPane.YES_NO_OPTION);
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
