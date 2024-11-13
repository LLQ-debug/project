package utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import java.awt.*;

public class WindowUtils {
    private static final User32 USER32 = User32.INSTANCE;

    public static void closeWindow(WinDef.HWND hwnd) {
        if (hwnd != null) {
            USER32.PostMessage(hwnd, User32.WM_CLOSE, null, null);
        }
    }


    /**
     * 获取窗口的坐标和大小
     *
     * @param hwnd 窗口句柄
     * @return 窗口的 Rectangle 对象
     */
    public static Rectangle getWindowBounds(WinDef.HWND hwnd) {
        WinDef.RECT rect = new WinDef.RECT();
        USER32.GetWindowRect(hwnd, rect);
        return new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    /**
     * 查找窗口句柄
     *
     * @param windowTitle 窗口标题
     * @return 窗口句柄
     */
    public static WinDef.HWND findWindow(String windowTitle) {
        return USER32.FindWindow(null, windowTitle);
    }

    /**
     * 等待指定时间内窗口出现
     *
     * @param windowTitle 窗口标题
     * @param timeoutInSeconds 等待超时时间（秒）
     * @return 如果找到窗口，返回窗口的 HWND；如果超时，返回 null
     */
    public static WinDef.HWND waitForWindow(String windowTitle, int timeoutInSeconds) {
        int elapsedSeconds = 0;
        int interval = 500; // 每隔500ms检查一次窗口
        while (elapsedSeconds < timeoutInSeconds * 1000) {
            WinDef.HWND hwnd = findWindow(windowTitle);
            if (hwnd != null) {
                return hwnd;
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            elapsedSeconds += interval;
        }
        System.out.println("超时未找到窗口：" + windowTitle);
        return null;
    }

    /**
     * 将窗口恢复到最前显示
     *
     * @param hwnd 窗口句柄
     */
    public static void bringWindowToFront(WinDef.HWND hwnd) {
        if (hwnd != null) {
            USER32.ShowWindow(hwnd, WinUser.SW_RESTORE);
            USER32.SetForegroundWindow(hwnd);
        }
    }
}
