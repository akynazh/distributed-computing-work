package com.jzh;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 信号分析结果显示器
 *
 * @author akyna
 * @date 04/04 004 5:15 PM
 */
public class SignalDisplay extends JPanel {
    // 最大信号展现量
    private final static int dataMaxSize = 20;
    // 存储最新信号
    private final static LinkedList<Double> signals = new LinkedList<>();
    private final ResOfSignalAnalysis res = new ResOfSignalAnalysis();

    public SignalDisplay() {
        res.setMin(Double.MAX_VALUE);
        res.setMax(Double.MIN_VALUE);
    }

    public void addDataPoint(double newData) {
        synchronized (signals) {
            signals.add(newData);
            if (signals.size() > dataMaxSize) {
                signals.removeFirst();
            }
        }
        repaint();
    }
    public void setNewResOfSignalAnalysis(ResOfSignalAnalysis newRes) {
        synchronized (res) {
            res.setAvg(newRes.getAvg());
            if (res.getMax() < newRes.getMax()) {
                res.setMax(newRes.getMax());
            }
            if (res.getMin() > newRes.getMin()) {
                res.setMin(newRes.getMin());
            }
            res.setVar(newRes.getVar());
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 50, offset = 30;
        synchronized (signals) {
            Iterator<Double> iter = signals.iterator();
            double pre = iter.hasNext() ? iter.next() : 0;
            while (iter.hasNext()) {
                double next = iter.next();
                int y1 = (int) (200 - pre);
                int y2 = (int) (200 - next);
                g.drawLine(x, y1, x + offset, y2);
                g.drawString(String.format("%.2f", pre), x, y1);
                g.drawString(String.format("%.2f", next), x + offset, y2);
                x += offset;
                pre = next;
            }
        }
        Font fontRes = new Font("Serif", Font.BOLD, 20);
        g.setFont(fontRes);
        synchronized (res) {
            g.drawString("过去" + SignalAnalyser.N + "个信号的均值: " + res.getAvg(), 50, 300);
            g.drawString("过去" + SignalAnalyser.N + "个信号的方差: " + res.getVar(), 50, 330);
            g.drawString("所有历史数据中的最小值:" + res.getMin(), 50, 360);
            g.drawString("所有历史数据中的最大值:" + res.getMax(), 50, 390);
        }
        Font fontTitle = new Font("Serif", Font.BOLD, 24);
        g.setFont(fontTitle);
        g.drawString("信号实时监测图", 175, 30);
    }

    public static void main(String[] args) throws Exception {
        // 初始化监测图
        SignalDisplay lineChart = new SignalDisplay();
        JFrame f = new JFrame();
        f.setContentPane(lineChart);
        f.setSize(800, 600);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 连接消息队列
        ConnectionFactory factory = new ConnectionFactory();
        Connection conn = factory.newConnection();
        Channel chan = conn.createChannel();
        chan.queueDeclare(MyQueues.SIGNAL_QUEUE_NAME, false, false, false, null);
        chan.queueDeclare(MyQueues.RES_QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallbackForSignal = (tag, delivery) -> {
            Double signal = Double.valueOf(new String(delivery.getBody(), StandardCharsets.UTF_8));
            System.out.println("收到信号: " + signal);
            lineChart.addDataPoint(signal);
        };
        DeliverCallback deliverCallbackForRes = (tag, delivery) -> {
            byte[] bytes = delivery.getBody();
            try (
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);
            ) {
                ResOfSignalAnalysis res = (ResOfSignalAnalysis) ois.readObject();
                System.out.println("收到分析结果: " + res);
                lineChart.setNewResOfSignalAnalysis(res);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        chan.basicConsume(MyQueues.SIGNAL_QUEUE_NAME, true, deliverCallbackForSignal, tag -> {
        });
        chan.basicConsume(MyQueues.RES_QUEUE_NAME, true, deliverCallbackForRes, tag -> {
        });
    }
}
