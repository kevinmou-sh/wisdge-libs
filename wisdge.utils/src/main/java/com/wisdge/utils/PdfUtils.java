package com.wisdge.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

@Slf4j
public class PdfUtils {
    private static final String CONVERT_SPENTTIME = "Convertion took time: {}'s";
    private static final String WATERMARKER_SPENTTIME = "Water marker took time: {}'s";
    private static final String SIGNMARKER_SPENTTIME = "Sign marker took time: {}'s";
    public static final String ITEXT_CHFONT_NAME = "STSongStd-Light";
    public static final String ITEXT_CHFONT_CODE = "UniGB-UCS2-H";
    public static final short WATER_MARKER = 0x01;
    public static final short OWNER_MARKER = 0x02;

    /**
     * 转换PDF文件到单一图片
     * @param inputStream PDF文件输入流
     * @return BufferedImage 图片对象
     */
    public static BufferedImage pdfToImage(InputStream inputStream) {
        int shiftWidth = 0; // 总宽度
        int shiftHeight = 0; // 总高度
        List<PdfUnit> unitArr = new ArrayList<>();
        BufferedImage imageResult = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        try {
            // 利用PdfBox生成图像
            PDDocument pdDocument = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            // 循环每个页码
            for (int i = 0, len = pdDocument.getNumberOfPages(); i < len; i++) {
                // DPI参数越大，越清晰
                BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
                int imageHeight = image.getHeight();
                int imageWidth = image.getWidth();
                if (i == 0) {
                    // 计算高度和偏移量
                    shiftWidth = imageWidth; // 使用第一张图片宽度
                }
                shiftHeight += imageHeight; // 计算偏移高度

                // 保存每页图片的像素值
                PdfUnit unit = new PdfUnit();
                unit.setHeight(imageHeight);
                unit.setRgbs(image.getRGB(0, 0, imageWidth, imageHeight, null, 0, shiftWidth));
                unitArr.add(unit);
            }
            pdDocument.close();

            imageResult = new BufferedImage(shiftWidth, shiftHeight, BufferedImage.TYPE_INT_RGB);
            int posY = 0;
            for(PdfUnit unit: unitArr) {
                imageResult.setRGB(0, posY, shiftWidth, unit.getHeight(), unit.getRgbs(), 0, shiftWidth);
                posY += unit.getHeight();
            }
        } catch (Exception e) {
            log.error(" 从PDF转换JPG格式异常!", e);
        }
        return imageResult;
    }

    /**
     * 给PDF加水印
     * @param pdfData PDF数据对象
     * @param txtMark 水印文字
     * @param txtSize 字体大小
     * @param markType 水印类型 WATER_MARKER/OWNER_MARKER
     * @param topLevel 水印是否包含第一页
     * @return 添加水印后的PDF数据对象
     */
    public static byte[] addMark(byte[] pdfData, String txtMark, int txtSize, short markType, boolean topLevel) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter pdfWriter = new PdfWriter(baos);
                ByteArrayInputStream bais = new ByteArrayInputStream(pdfData);
        ) {
            long old = System.currentTimeMillis();
            PdfReader pdfReader = new PdfReader(bais);
            PdfDocument inDocument = new PdfDocument(pdfReader);
            PdfDocument outDocument = new PdfDocument(pdfWriter);

            IEventHandler handler = (markType == WATER_MARKER) ? new WaterMark(txtMark, txtSize, topLevel) : new OwnerMark(txtMark, txtSize, topLevel);
            outDocument.addEventHandler(PdfDocumentEvent.INSERT_PAGE, handler);
            int numberOfPages = inDocument.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                PdfPage page = inDocument.getPage(i);
                outDocument.addPage(page.copyTo(outDocument));
            }

            outDocument.close();
            inDocument.close();
            pdfReader.close();
            long now = System.currentTimeMillis();
            if (markType == WATER_MARKER)
                log.debug(WATERMARKER_SPENTTIME, ((now - old) / 1000.0));
            else
                log.debug(SIGNMARKER_SPENTTIME, ((now - old) / 1000.0));
            return baos.toByteArray();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    public static byte[] errorPage(String message) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer)
        ) {
            //处理中文问题
            PdfFont font = PdfFontFactory.createFont(ITEXT_CHFONT_NAME, ITEXT_CHFONT_CODE);
            Paragraph p =new Paragraph(message);
            p.setFontColor(ColorConstants.RED);
            p.setFont(font);
            p.setFontSize(12);
            Document document = new Document(pdf);
            document.add(p);
            document.close();
            return baos.toByteArray();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }
}

@Data
class PdfUnit {
    private int height;
    private int[] rgbs;
}



/**
 * 监听事件 添加水印
 */
@Slf4j
class WaterMark implements IEventHandler {
    private String txtMark;
    private int txtSize;
    private boolean topLevel;

    public WaterMark(String txtMark, int txtSize, boolean topLevel) {
        this.txtMark = txtMark;
        this.txtSize = txtSize;
        this.topLevel = topLevel;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        PdfDocument document = documentEvent.getDocument();
        PdfPage pdfPage = documentEvent.getPage();
        PageSize pageSize = document.getDefaultPageSize();
        try {
            PdfFont pdfFont = PdfFontFactory.createFont(PdfUtils.ITEXT_CHFONT_NAME, PdfUtils.ITEXT_CHFONT_CODE);
            PdfCanvas pdfCanvas = new PdfCanvas(topLevel ? pdfPage.newContentStreamAfter() : pdfPage.newContentStreamBefore(), pdfPage.getResources(), document);
            Paragraph paragraph = new Paragraph(txtMark);
            paragraph.setRotationAngle(170);
            Canvas canvas = new Canvas(pdfCanvas, pdfPage.getPageSize());
            canvas.setFont(pdfFont);
            canvas.setFontSize(txtSize);
            canvas.setFontColor(ColorConstants.LIGHT_GRAY, 0.3f);
            canvas.showTextAligned(paragraph, pageSize.getWidth()/2, pageSize.getHeight()/2, TextAlignment.CENTER, VerticalAlignment.MIDDLE);
            canvas.close();
            pdfCanvas.release();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

/**
 * 监听事件 添加所属人
 */
@Slf4j
class OwnerMark implements IEventHandler {
    private String txtMark;
    private int txtSize;
    private boolean topLevel;

    public OwnerMark(String txtMark, int txtSize, boolean topLevel) {
        this.txtMark = txtMark;
        this.txtSize = txtSize;
        this.topLevel = topLevel;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDocument = documentEvent.getDocument();
        PdfPage pdfPage = documentEvent.getPage();
        try {
            PdfFont pdfFont = PdfFontFactory.createFont(PdfUtils.ITEXT_CHFONT_NAME, PdfUtils.ITEXT_CHFONT_CODE);
            if (topLevel) {
                PdfCanvas pdfCanvas = new PdfCanvas(pdfPage.newContentStreamAfter(), pdfPage.getResources(), pdfDocument);
                Canvas canvas = new Canvas(pdfCanvas.stroke(), pdfPage.getPageSize());
                canvas.setFont(pdfFont);
                canvas.setFontSize(txtSize);
                canvas.setFontColor(ColorConstants.LIGHT_GRAY, 0.5f);
                canvas.showTextAligned(txtMark, 10, 10, TextAlignment.LEFT);
                canvas.close();
                pdfCanvas.release();
            } else {
                PdfCanvas pdfCanvas = new PdfCanvas(pdfPage.newContentStreamBefore(), pdfPage.getResources(), pdfDocument);
                Canvas canvas = new Canvas(pdfCanvas, pdfPage.getPageSize());
                canvas.setFont(pdfFont);
                canvas.setFontSize(txtSize);
                canvas.setFontColor(new DeviceRgb(168, 168, 168));
                canvas.showTextAligned(txtMark, 10, 10, TextAlignment.LEFT);
                canvas.close();
                pdfCanvas.release();
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
