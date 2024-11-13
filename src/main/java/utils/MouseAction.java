package utils;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseAction {

    /**
     * 模拟鼠标点击操作
     * @param x 屏幕全局 X 坐标
     * @param y 屏幕全局 Y 坐标
     * @throws Exception 如果鼠标操作失败
     */
    public static void simulateClick(int x, int y) throws Exception {
        Robot robot = new Robot();
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
