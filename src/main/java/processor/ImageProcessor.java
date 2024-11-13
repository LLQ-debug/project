package processor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 加载 OpenCV 库
    }

    /**
     * 将图像转换为灰度图
     */
    public Mat convertToGray(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    /**
     * 高斯模糊去噪
     */
    public Mat applyGaussianBlur(Mat image, int kernelSize) {
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(image, blurred, new Size(kernelSize, kernelSize), 0);
        return blurred;
    }

    /**
     * 应用边缘检测
     */
    public Mat applyEdgeDetection(Mat image, int threshold1, int threshold2) {
        Mat edges = new Mat();
        Imgproc.Canny(image, edges, threshold1, threshold2);
        return edges;
    }

    /**
     * 缩放图像
     */
    public Mat resizeImage(Mat image, double scale) {
        Mat resized = new Mat();
        Size newSize = new Size(image.cols() * scale, image.rows() * scale);
        Imgproc.resize(image, resized, newSize);
        return resized;
    }
}
