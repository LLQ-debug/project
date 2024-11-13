package main;

import com.sun.jna.platform.win32.WinDef;
import matcher.ImageMatcher;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import processor.ImageProcessor;
import ui.MainWindow;
import utils.MouseAction;
import utils.WindowUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameAutomationMain {

    private static volatile boolean running = true; // 控制循环的标志位
    private static volatile boolean paused = false; // 控制暂停的标志位

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    // 自动化运行方法
    public static void runAutomation(String cardKey, MainWindow ui) {
        try {
            ui.appendLog("卡密输入成功，启动自动化流程...");
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            ImageProcessor processor = new ImageProcessor();
            ImageMatcher matcher = new ImageMatcher();

            String launcherWindowName = "launcher";
            WinDef.HWND launcherHwnd = WindowUtils.findWindow(launcherWindowName);
            if (launcherHwnd == null) {
                ui.appendLog("未找到登录器窗口：" + launcherWindowName);
                return;
            }
            Rectangle launcherBounds = WindowUtils.getWindowBounds(launcherHwnd);
            ui.appendLog("登录器窗口位置：" + launcherBounds);

            String startButtonPath = "E:\\wanguo\\src\\main\\resources\\tessdata\\template.png";
            if (clickButtonInWindow(startButtonPath, launcherBounds, matcher, processor, ui)) {
                ui.appendLog("点击了'开始游戏'按钮");
            }

            String gameWindowName = "万国觉醒";
            WinDef.HWND gameHwnd = WindowUtils.waitForWindow(gameWindowName, 10);
            if (gameHwnd == null) {
                ui.appendLog("未找到游戏窗口，退出！");
                return;
            }
            Rectangle gameBounds = WindowUtils.getWindowBounds(gameHwnd);
            ui.appendLog("检测到游戏窗口：" + gameBounds);

            List<String> templatePaths = Arrays.asList(
                    "E:\\wanguo\\src\\main\\resources\\tessdata\\template1.png",
                    "E:\\wanguo\\src\\main\\resources\\tessdata\\template2.png",
                    "E:\\wanguo\\src\\main\\resources\\tessdata\\template3.png"
            );

            // 自动化任务循环
            while (running) {
                // 检查是否暂停
                while (paused) {
                    Thread.sleep(100); // 暂停时减少 CPU 占用
                }

                // 执行按钮检测和点击操作
                for (String templatePath : templatePaths) {
                    if (clickButtonInWindow(templatePath, gameBounds, matcher, processor, ui)) {
                        ui.appendLog("点击了按钮：" + templatePath);
                        executeActionForTemplate(templatePath, gameBounds, matcher, processor, ui);
                    }
                }

                Thread.sleep(5000); // 设置循环间隔，防止过于频繁
            }

            ui.appendLog("自动化任务已停止。");

        } catch (Exception e) {
            ui.appendLog("程序运行出错：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopAutomation() {
        running = false;
    }

    public static void togglePause() {
        paused = !paused;
    }

    public static boolean isPaused() {
        return paused;
    }


    /**
     * 点击窗口内的按钮
     */
    public static boolean clickButtonInWindow(String templatePath, Rectangle windowBounds,
                                              ImageMatcher matcher, ImageProcessor processor, MainWindow ui) throws Exception {
        Mat sourceImage = captureScreenshot(windowBounds);
        if (sourceImage.empty()) {
            ui.appendLog("截图失败！");
            return false;
        }

        Mat templateImage = Imgcodecs.imread(templatePath);
        if (templateImage.empty()) {
            ui.appendLog("模板图片加载失败: " + templatePath);
            return false;
        }

        // 进行模板匹配
        Mat preprocessedSource = processor.convertToGray(sourceImage);
        Mat preprocessedTemplate = processor.convertToGray(templateImage);
        Point matchLoc = matcher.multiScaleTemplateMatch(preprocessedSource, preprocessedTemplate, 0.8, 1.2, 0.05, 0.8);

        if (matchLoc != null) {
            int templateWidth = templateImage.width();
            int templateHeight = templateImage.height();
            int centerX = (int) matchLoc.x + templateWidth / 2;
            int centerY = (int) matchLoc.y + templateHeight / 2;

            int offsetX = (int) (Math.random() * 21) - 10;
            int offsetY = (int) (Math.random() * 21) - 10;
            int globalClickX = windowBounds.x + centerX + offsetX;
            int globalClickY = windowBounds.y + centerY + offsetY;

            MouseAction.simulateClick(globalClickX, globalClickY);
            return true;
        } else {
            ui.appendLog("未找到按钮：" + templatePath);
            return false;
        }
    }

    /**
     * 根据模板路径执行相应的操作
     */
    public static void executeActionForTemplate(String templatePath, Rectangle gameBounds,
                                                ImageMatcher matcher, ImageProcessor processor, MainWindow ui) throws AWTException {
        switch (templatePath) {
            case "E:\\wanguo\\src\\main\\resources\\tessdata\\template1.png":
                ui.appendLog("执行动作链 1");
                break;

            case "E:\\wanguo\\src\\main\\resources\\tessdata\\template2.png":
                ui.appendLog("执行动作链 2");
                break;

            case "E:\\wanguo\\src\\main\\resources\\tessdata\\template3.png":
                ui.appendLog("执行动作链 3");
                break;

            default:
                ui.appendLog("未定义的模板：" + templatePath);
        }
    }

    /**
     * 截取指定区域的屏幕截图并转换为 OpenCV Mat 格式
     */
    public static Mat captureScreenshot(Rectangle rect) throws AWTException {
        Robot robot = new Robot();
        BufferedImage bufferedImage = robot.createScreenCapture(rect);
        return bufferedImageToMat(bufferedImage);
    }

    /**
     * 将 BufferedImage 转换为 OpenCV Mat 格式
     */
    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            return Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_COLOR);
        } catch (IOException e) {
            System.out.println("BufferedImage 转换为 Mat 失败：" + e.getMessage());
            return new Mat();
        }
    }
}
