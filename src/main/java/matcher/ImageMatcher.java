package matcher;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core.MinMaxLocResult;

public class ImageMatcher {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 加载 OpenCV 库
    }

    /**
     * 单尺度模板匹配
     */
    public Point matchTemplate(Mat sourceImage, Mat templateImage, int matchMethod, double threshold) {
        Mat result = new Mat();
        Imgproc.matchTemplate(sourceImage, templateImage, result, matchMethod);

        MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED)
                ? mmr.minLoc : mmr.maxLoc;

        double matchScore = (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED)
                ? mmr.minVal : mmr.maxVal;

        if (matchScore >= threshold) {
            return matchLoc;
        }
        return null; // 未找到匹配
    }

    /**
     * 多尺度模板匹配
     */
    public Point multiScaleTemplateMatch(Mat sourceImage, Mat templateImage, double minScale, double maxScale, double scaleStep, double threshold) {
        Point bestMatchLoc = null;
        double bestMatchScore = -1;

        for (double scale = minScale; scale <= maxScale; scale += scaleStep) {
            Mat resizedTemplate = new Mat();
            Size newSize = new Size(templateImage.cols() * scale, templateImage.rows() * scale);
            Imgproc.resize(templateImage, resizedTemplate, newSize);

            Mat result = new Mat();
            Imgproc.matchTemplate(sourceImage, resizedTemplate, result, Imgproc.TM_CCOEFF_NORMED);

            MinMaxLocResult mmr = Core.minMaxLoc(result);

            if (mmr.maxVal > bestMatchScore && mmr.maxVal >= threshold) {
                bestMatchScore = mmr.maxVal;
                bestMatchLoc = mmr.maxLoc;
            }
        }

        return bestMatchLoc;
    }
}
