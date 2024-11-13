package ui;

import com.sun.jna.platform.win32.WinDef;
import main.GameAutomationMain;
import utils.WindowUtils;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.TimerTask;

public class MainWindow extends JFrame {
    private JTextArea logArea;
    private JTextField cardKeyField;
    private JLabel runtimeLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton scheduleLogoutButton;
    private LocalDateTime startTime;
    private Timer logoutTimer;

    public MainWindow() {
        setTitle("Game Automation Controller");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("日志输出"));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("卡密输入"));
        cardKeyField = new JTextField(20);
        inputPanel.add(new JLabel("卡密: "));
        inputPanel.add(cardKeyField);

        runtimeLabel = new JLabel("运行时间: 00:00:00");
        JPanel runtimePanel = new JPanel(new FlowLayout());
        runtimePanel.add(runtimeLabel);

        startButton = new JButton("启动自动化");
        startButton.addActionListener(new StartButtonListener());

        pauseButton = new JButton("暂停");
        pauseButton.addActionListener(new PauseButtonListener());

        scheduleLogoutButton = new JButton("定时下线");
        scheduleLogoutButton.addActionListener(new ScheduleLogoutButtonListener());

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(scheduleLogoutButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(runtimePanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startTime = LocalDateTime.now();
            updateRuntime();

            String cardKey = cardKeyField.getText();
            if (cardKey.isEmpty()) {
                appendLog("请输入卡密后再启动！");
                return;
            }

            appendLog("启动自动化流程...");
            new Thread(() -> GameAutomationMain.runAutomation(cardKey, MainWindow.this)).start();
        }
    }

    private class PauseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GameAutomationMain.togglePause();
            if (GameAutomationMain.isPaused()) {
                pauseButton.setText("继续");
                appendLog("自动化流程已暂停。");
            } else {
                pauseButton.setText("暂停");
                appendLog("自动化流程已继续。");
            }
        }
    }


    private class ScheduleLogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(MainWindow.this, "请输入下线时间（秒）:");
            if (input != null && !input.isEmpty()) {
                int seconds = Integer.parseInt(input);

                // 如果已经有一个定时任务，先停止它
                if (logoutTimer != null) {
                    logoutTimer.stop();
                }

                // 创建一个新的定时任务
                logoutTimer = new Timer(seconds * 1000, event -> {
                    appendLog("定时下线触发，自动化流程即将停止。");
                    GameAutomationMain.stopAutomation();
                    closeGameWindow();
                    logoutTimer.stop();  // 执行后停止定时器
                });
                logoutTimer.setRepeats(false);  // 确保定时任务只执行一次
                logoutTimer.start();

                appendLog("定时下线已设置，" + seconds + " 秒后自动停止。");
            }
        }
    }


    private void updateRuntime() {
        Timer timer = new Timer(1000, e -> {
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;
            runtimeLabel.setText(String.format("运行时间: %02d:%02d:%02d", hours, minutes, seconds));
        });
        timer.start();
    }

    public void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void closeGameWindow() {
        WinDef.HWND hwnd = WindowUtils.findWindow("万国觉醒");
        if (hwnd != null) {
            WindowUtils.closeWindow(hwnd); // 假设WindowUtils类中实现了closeWindow方法
            appendLog("游戏窗口已关闭。");
        } else {
            appendLog("未找到游戏窗口。");
        }
    }
}
