package ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class OCRHelper {

    private ITesseract tesseract;

    public OCRHelper(String tessDataPath, String language) {
        tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath); // 设置 Tesseract 数据路径
        tesseract.setLanguage(language); // 设置语言
    }

    /**
     * 识别图片中的文字
     */
    public String recognizeText(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }
}
