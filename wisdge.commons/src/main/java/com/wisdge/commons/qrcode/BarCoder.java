package com.wisdge.commons.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.wisdge.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class BarCoder {
	public static byte[] encode(String content, int width, int height) throws Exception {
		return encode(content, width, height, null);
	}

	/**
	 * 条形码编码
	 *
	 * @param content String
	 * @param width int
	 * @param height int
	 * @param format String
	 * @return byte[] data of BarCode image
	 */
	public static byte[] encode(String content, int width, int height, String format) throws Exception {
		int codeWidth = 3 + // start guard
				(7 * 6) + // left bars
				5 + // middle guard
				(7 * 6) + // right bars
				3; // end guard
		codeWidth = Math.max(codeWidth, width);
		BarcodeFormat barcodeFormat = getFormat(format);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, barcodeFormat, codeWidth, height, null);
		return ImageUtils.getBytesFromImage(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG");
	}
	
	private static BarcodeFormat getFormat(String format) {
		if (format == null)
			return BarcodeFormat.EAN_13;
		
		format = format.toUpperCase();
		switch(format) {
		case "AZTEC":
			return BarcodeFormat.AZTEC;
		case "CODABAR":
			return BarcodeFormat.CODABAR;
		case "CODE_128":
		case "CODE128":
			return BarcodeFormat.CODE_128;
		case "CODE_39":
		case "CODE39":
			return BarcodeFormat.CODE_39;
		case "CODE_93":
		case "CODE93":
			return BarcodeFormat.CODE_93;
		case "DATA_MATRIX":
			return BarcodeFormat.DATA_MATRIX;
		case "EAN_8":
			return BarcodeFormat.EAN_8;
		case "ITF":
			return BarcodeFormat.ITF;
		case "MAXICODE":
			return BarcodeFormat.MAXICODE;
		case "PDF_417":
		case "PDF417":
			return BarcodeFormat.PDF_417;
		case "QR_CODE":
			return BarcodeFormat.QR_CODE;
		case "RSS_14":
		case "RSS14":
			return BarcodeFormat.RSS_14;
		case "RSS_EXPANDED":
			return BarcodeFormat.RSS_EXPANDED;
		case "UPC_A":
		case "UPCA":
			return BarcodeFormat.UPC_A;
		case "UPC_E":
		case "UPCE":
			return BarcodeFormat.UPC_E;
		case "UPC_EAN_EXTENSION":
		case "UPCEANEXTENSION":
			return BarcodeFormat.UPC_EAN_EXTENSION;
		default:
			return BarcodeFormat.EAN_13;
		}
	}
 
	/**
	 * 条形码解码
	 * 
	 * @param imgData byte[] data of BarCode image
	 * @return String code string
	 */
	public static String decode(byte[] imgData) throws Exception {
		BufferedImage image = ImageUtils.getImageFromBytes(imgData);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result = new MultiFormatReader().decode(bitmap, null);
		return result.getText();
	}
}
