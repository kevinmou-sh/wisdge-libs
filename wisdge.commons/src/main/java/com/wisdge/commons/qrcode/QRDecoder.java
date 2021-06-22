package com.wisdge.commons.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码解码类
 *
 * @author Kevin MOU
 */
public class QRDecoder {
	public static Logger logger = LoggerFactory.getLogger(QRDecoder.class);

	/**
	 * 对一个目录文件进行解码
	 *
	 * @param filepath
	 *            目标文件的地址
	 * @return 解码后的字符串
	 */
	public static String decode(String filepath) {
		return decode(filepath, false);
	}
	public static String decode(String filepath, boolean barCode) {
		try {
			BufferedImage image = ImageIO.read(new File(filepath));
			return decode(image, barCode);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 对一个BufferedImage对象进行解码
	 *
	 * @param image
	 *            目标BufferedImage对象
	 * @return 解码后的字符串
	 */
	public static String decode(BufferedImage image) {
		return decode(image, false);
	}

	public static String decode(BufferedImage image, boolean barCode) {
		try {
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
	        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码

	        Result result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), hints);
//			Result result = new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(source)), hints);
			return result.getText();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 对一个数组进行解码
	 *
	 * @param bytes
	 *            目标数据byte[]对象
	 * @return 解码后的字符串
	 * @see #decode(Image)
	 */
	public static String decode(byte[] bytes) {
		return decode(bytes, false);
	}
	public static String decode(byte[] bytes, boolean barCode) {
		return decode(getImageFromBytes(bytes), barCode);
	}

	/**
	 * 对一个Image对象进行解码
	 *
	 * @param image
	 *            目标Image对象
	 * @return 解码后的字符串
	 * @see #decode(Image)
	 */
	public static String decode(Image image) {
		return decode(image2Buffered(image, false));
	}


	/**
	 * 从数组中获得Image对象.
	 *
	 * @param buffer
	 *            图像数组，可能来自于文件读取或数据库
	 * @return Image对象.
	 */
	public static BufferedImage getImageFromBytes(byte[] buffer) {
		try {
			ByteArrayInputStream bos = new ByteArrayInputStream(buffer);
			return ImageIO.read(bos);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * This method returns an Image object from a buffered image
	 *
	 * @param bufferedImage
	 *            源BufferedImage对象
	 * @return 目标Image对象
	 */
	public static Image buffered2Image(BufferedImage bufferedImage) {
		return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	}

	/**
	 * This method returns a buffered image from an Image object
	 *
	 * @param image
	 *            目标Image对象
	 * @param translucent
	 *            透明背景
	 * @return 目标BufferedImage对象
	 */
	public static BufferedImage image2Buffered(Image image, boolean translucent) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		if (translucent) {
			bi = g2d.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = bi.createGraphics();
		}
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return bi;
	}
}
