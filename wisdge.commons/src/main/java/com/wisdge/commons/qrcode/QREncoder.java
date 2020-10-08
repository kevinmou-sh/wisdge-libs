package com.wisdge.commons.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * 二维码图片生成类
 * 
 * @author Kevin MOU
 */
public class QREncoder {
	public static Logger logger = LoggerFactory.getLogger(QREncoder.class);
	private static MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

	public static BufferedImage encode2Image(String content) {
		return encode2Image(content, "UTF-8");
	}

	public static BufferedImage encode2Image(String content, String charsetName) {
		return encode2Image(content, charsetName, 258, 258);
	}

	/**
	 * 对字符串按照格式进行编码
	 * 
	 * @param content     目标字符串对象
	 * @param charsetName 国际编码格式
	 * @param width       二维码图像宽度
	 * @param height      二维码图像高度
	 * @return 编码后的BufferedImage对象
	 */
	public static BufferedImage encode2Image(String content, String charsetName, int width, int height) {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, charsetName);
		hints.put(EncodeHintType.MARGIN, 0);

		try {
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			return MatrixToImageWriter.toBufferedImage(bitMatrix);
		} catch (WriterException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static void encode2File(String content, String imageFormat, String filepath) throws WriterException, IOException {
		encode2File(content, imageFormat, filepath, "GBK");
	}

	public static void encode2File(String content, String imageFormat, String filepath, String charsetName) throws WriterException, IOException {
		encode2File(content, imageFormat, filepath, charsetName, 258, 258);
	}

	/**
	 * 对字符串按照格式进行编码，并保存入文件
	 * 
	 * @param content     目标字符串
	 * @param imageFormat 保存图片文件的格式
	 * @param filepath    图片文件的路径
	 * @param charsetName 国际编码格式
	 * @param width       二维码宽度
	 * @param height      二维码高度
	 * @return 编码成功并保存到指定的文件返回true，否则返回false
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void encode2File(String content, String imageFormat, String filepath, String charsetName, int width, int height) throws WriterException, IOException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, charsetName);

		BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		File file = new File(filepath);
		OutputStream os = new FileOutputStream(file);
		try {
			MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, os);
		} finally {
			os.close();
		}
	}

	public static void encode2Stream(String content, String imageFormat, OutputStream stream) throws WriterException, IOException {
		encode2Stream(content, imageFormat, stream, "GBK");
	}

	public static void encode2Stream(String content, String imageFormat, OutputStream stream, String charsetName) throws WriterException, IOException {
		encode2Stream(content, imageFormat, stream, charsetName, 400, 400);
	}

	/**
	 * 对字符串按照格式进行编码，并写入流
	 * 
	 * @param content     目标字符串
	 * @param imageFormat 保存图片文件的格式
	 * @param stream      被写入二维码的流
	 * @param charsetName 国际编码格式
	 * @param width       二维码宽度
	 * @param height      二维码高度
	 * @return 编码成功并成功写入流返回true，否则返回false
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void encode2Stream(String content, String imageFormat, OutputStream stream, String charsetName, int width, int height) throws WriterException, IOException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, charsetName);

		BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, stream);
	}

	public static void encode2Stream(String content, BufferedImage logoImage, String imageFormat, OutputStream stream) throws WriterException, IOException {
		encode2Stream(content, logoImage, imageFormat, stream, "GBK");
	}

	public static void encode2Stream(String content, BufferedImage logoImage, String imageFormat, OutputStream stream, String charsetName) throws WriterException, IOException {
		encode2Stream(content, logoImage, imageFormat, stream, charsetName, 400, 400);
	}

	/**
	 * 对字符串按照格式进行编码，并写入流
	 * 
	 * @param content     目标字符串
	 * @param logoImage   logo图片
	 * @param imageFormat 保存图片文件的格式
	 * @param stream      被写入二维码的流
	 * @param charsetName 国际编码格式
	 * @param width       二维码宽度
	 * @param height      二维码高度
	 * @return 编码成功并成功写入流返回true，否则返回false
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void encode2Stream(String content, BufferedImage logoImage, String imageFormat, OutputStream stream, String charsetName, int width, int height) throws WriterException, IOException {
		BufferedImage qrcodeImage = createEncode2Image(content, logoImage, charsetName, width, height);
		if (!ImageIO.write(qrcodeImage, imageFormat, stream)) {
			throw new IOException("Could not write qrcode to format " + imageFormat);
		}
	}

	public static BufferedImage createEncode2Image(String content, BufferedImage logoImage, String charsetName, int width, int height) throws WriterException, IOException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, charsetName);

		BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		BufferedImage qrcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 开始利用二维码数据创建Bitmap图片，分别设为白（0xFFFFFFFF）黑（0xFF000000）两色
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				qrcodeImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		return LogoConfig.getLogoMatrix(qrcodeImage, logoImage);
	}

	/**
	 * 对字符串按照格式进行编码，并写入流
	 * 
	 * @param content     目标字符串
	 * @param imageFormat 保存图片文件的格式
	 * @param stream      被写入二维码的流
	 * @param charsetName 国际编码格式
	 * @param width       二维码宽度
	 * @param height      二维码高度
	 * @return 编码成功并成功写入流返回true，否则返回false
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void encode2StreamColor(String content, String imageFormat, OutputStream stream, String charsetName, int width, int height) throws WriterException, IOException {
		encode2StreamColor(content, null, imageFormat, stream, charsetName, width, height);
	}

	/**
	 * 对字符串按照格式进行编码，并写入流
	 * 
	 * @param content     目标字符串
	 * @param logoImage   logo图片
	 * @param imageFormat 保存图片文件的格式
	 * @param stream      被写入二维码的流
	 * @param charsetName 国际编码格式
	 * @param width       二维码宽度
	 * @param height      二维码高度
	 * @return 编码成功并成功写入流返回true，否则返回false
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void encode2StreamColor(String content, BufferedImage logoImage, String imageFormat, OutputStream stream, String charsetName, int width, int height) throws WriterException, IOException {
		BufferedImage qrCodeImage = createEnCode2ImageColor(content, logoImage, charsetName, width, height);
		if (!ImageIO.write(qrCodeImage, imageFormat, stream)) {
			throw new IOException("Could not write qrcode to format " + imageFormat);
		}
	}

	public static BufferedImage createEnCode2ImageColor(String content, BufferedImage logoImage, String charsetName, int width, int height) throws WriterException, IOException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, charsetName);
		BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

		boolean flag1 = true;
		int stopx = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (bitMatrix.get(x, y)) {
					if (flag1) {
						flag1 = false;
					}
				} else {
					if (flag1 == false) {
						stopx = x;
						break;
					}
				}
			}
			if (flag1 == false)
				break;
		}

		// 二维矩阵转为一维像素数组
		int[] pixels = new int[width * height];
		for (int y = 0; y < bitMatrix.getHeight(); y++) {
			for (int x = 0; x < bitMatrix.getWidth(); x++) {
				if (x > 0 && x < stopx && y > 0 && y < stopx) {
					// 左上角颜色,根据自己需要调整颜色范围和颜色
					Color color = new Color(231, 144, 56);
					int colorInt = color.getRGB();
					pixels[y * width + x] = bitMatrix.get(x, y) ? colorInt : 16777215;
				} else {
					// 二维码颜色
					int num1 = (int) (50 - (50.0 - 13.0) / bitMatrix.getHeight() * (y + 1));
					int num2 = (int) (165 - (165.0 - 72.0) / bitMatrix.getHeight() * (y + 1));
					int num3 = (int) (162 - (162.0 - 107.0) / bitMatrix.getHeight() * (y + 1));
					Color color = new Color(num1, num2, num3);
					int colorInt = color.getRGB();
					// 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
					pixels[y * width + x] = bitMatrix.get(x, y) ? colorInt : 16777215; // 0x000000:0xffffff
				}
			}
		}
		BufferedImage qrcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		qrcodeImage.getRaster().setDataElements(0, 0, width, height, pixels);
		if (logoImage != null)
			return LogoConfig.getLogoMatrix(qrcodeImage, logoImage);
		else
			return qrcodeImage;
	}
}
