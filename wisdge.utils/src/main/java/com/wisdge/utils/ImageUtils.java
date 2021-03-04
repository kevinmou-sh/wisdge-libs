package com.wisdge.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 * AWT Image utility class &copy;2012 Wisdge.com
 * 
 * @author Kevin.MOU
 * @version 1.3 Last Modified: 2014/03/02
 */
public class ImageUtils {

	/**
	 * 从BYTE[]数组中获得一个重新定义尺寸的Image对象.
	 * 
	 * @param buffer
	 *            图像数组，可能来自于文件读取或数据库
	 * @param unitW
	 *            新Image对象的图像宽度.
	 * @param unitH
	 *            新Image对象的图像高度.
	 * @param scaled
	 *            true=当原图小于目标尺寸时不进行拉伸，false=小于目标尺寸时拉伸
	 * @return 重新定义尺寸的Image对象.
	 * @throws IOException 
	 */
	public static Image getResizeImage(byte[] buffer, int unitW, int unitH, boolean scaled) throws IOException {
		BufferedImage bi = getImageFromBytes(buffer);
		return getResizeImage(bi, unitW, unitH, scaled);
	}

	/**
	 * 从Image中获得一个重新定义尺寸的Image对象.
	 * 
	 * @param image
	 *            图像数组，可能来自于文件读取或数据库
	 * @param unitW
	 *            新Image对象的图像宽度.
	 * @param unitH
	 *            新Image对象的图像高度.
	 * @param scaled
	 *            当图片小于目标尺寸时候是否需要拉伸
	 * @return 重新定义尺寸的Image对象.
	 */
	public static BufferedImage getResizeImage(BufferedImage image, int unitW, int unitH, boolean scaled) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		if (!scaled && w < unitW && h < unitH) {
			return image;
		}

		int cw, ch;
		// 判断原图长宽比例
		if ((float) w / h > (float) unitW / unitH) { // // 原图长宽比例中, 宽度为优
			if (w > unitW) {
				cw = unitW;
			} else {
				cw = w;
			}
			ch = (int) ((float) h / w * cw);
		} else { // 原图长宽比例中，高度为优
			if (h > unitH) {
				ch = unitH;
			} else {
				ch = h;
			}
			cw = (int) ((float) w / h * ch);
		}
		// System.out.println("unitW="+unitW+", unitH="+unitH+", orW="+w+",
		// oH="+h+", cW="+cw+", cH="+ch);
		Image scaleImage = image.getScaledInstance(cw, ch, Image.SCALE_DEFAULT); // 得到一个(cw*ch)的图像
		return Image2Buffered(scaleImage, (image.getTransparency() == BufferedImage.TRANSLUCENT));
	}

	/**
	 * 从文件名判断图片类型
	 * 
	 * @param filename
	 *            文件名
	 * @return 图片类型
	 */
	public static String getImageType(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		if (extension.equalsIgnoreCase("PNG")) {
			return "PNG";
		} else if (extension.equalsIgnoreCase("GIF")) {
			return "GIF";
		} else {
			return "JPG";
		}
	}

	/**
	 * 从Image中获得图像数组.
	 * 
	 * @param image
	 *            Image对象，用于获取图像数组
	 * @param suffix
	 *            图片类型：JPG, PNG, GIF
	 * @return 数据对象 byte[].
	 */
	public static byte[] getBytesFromImage(Image image, String suffix) {
		return getBytesFromImage(Image2Buffered(image, (suffix.equalsIgnoreCase("PNG") || suffix.equalsIgnoreCase("GIF"))), suffix);
	}

	public static byte[] getBytesFromImage(BufferedImage bufferedImage, String suffix) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, suffix, bos);
			return bos.toByteArray();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	/**
	 * 从数组中获得Image对象.
	 * 
	 * @param buffer
	 *            图像数组，可能来自于文件读取或数据库
	 * @return Image对象.
	 * @throws IOException 
	 */
	public static BufferedImage getImageFromBytes(byte[] buffer) throws IOException {
		ByteArrayInputStream bos = new ByteArrayInputStream(buffer);
		return ImageIO.read(bos);
	}

	/**
	 * This method returns an Image object from a buffered image
	 * 
	 * @param bufferedImage
	 *            源BufferedImage对象
	 * @return 目标Image对象
	 */
	public static Image Buffered2Image(BufferedImage bufferedImage) {
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
	public static BufferedImage Image2Buffered(Image image, boolean translucent) {
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

	/**
	 * 将图像转换为黑白色图片.
	 * 
	 * @param source
	 *            源图像 BufferedImage对象
	 * @return 目标 BufferedImage对象
	 */
	public static BufferedImage grayImage(BufferedImage source) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(source, null);
	}

	/**
	 * 同比格式化宽度和高度
	 * 
	 * @param fixWidth
	 *            目标宽度
	 * @param fixHeight
	 *            目标高度
	 * @param srcWidth
	 *            原宽度
	 * @param srcHeight
	 *            原高度
	 * @return 格式后的尺寸，int[]{width, height}
	 */
	public static int[] getFixSize(int fixWidth, int fixHeight, int srcWidth, int srcHeight) {
		int cw, ch;
		// 判断原图长宽比例
		if ((float) srcWidth / srcHeight > (float) fixWidth / fixHeight) { // // 原图长宽比例中, 宽度为优
			if (srcWidth > fixWidth) {
				cw = fixWidth;
			} else {
				cw = srcWidth;
			}
			ch = (int) ((float) srcHeight / srcWidth * cw);
		} else { // 原图长宽比例中，高度为优
			if (srcHeight > fixHeight) {
				ch = fixHeight;
			} else {
				ch = srcHeight;
			}
			cw = (int) ((float) srcWidth / srcHeight * ch);
		}
		return new int[] { cw, ch };
	}

	/**
	 * 图像切割(按指定起点坐标和宽高切割)
	 * 
	 * @param image
	 *            源图像
	 * @param x
	 *            目标切片起点坐标X
	 * @param y
	 *            目标切片起点坐标Y
	 * @param width
	 *            目标切片宽度
	 * @param height
	 *            目标切片高度
	 * @return Image
	 */
	public static Image cut(Image image, int x, int y, int width, int height) {
		// 四个参数分别为图像起点坐标和宽高
		// 即: CropImageFilter(int x,int y,int width,int height)
		ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
		return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
	}

	/**
	 * 图像切割（指定切片的行数和列数）
	 * 
	 * @param image
	 *            源图像
	 * @param rows
	 *            目标切片行数。默认2，必须是范围 [1, 20] 之内
	 * @param cols
	 *            目标切片列数。默认2，必须是范围 [1, 20] 之内
	 * @return List
	 */
	public static List<Image> cut2(Image image, int rows, int cols) {
		List<Image> result = new ArrayList<Image>();
		int srcWidth = image.getWidth(null);
		int srcHeight = image.getHeight(null);
		ImageFilter cropFilter;
		int destWidth = srcWidth; // 每张切片的宽度
		int destHeight = srcHeight; // 每张切片的高度
		// 计算切片的宽度和高度
		if (srcWidth % cols == 0) {
			destWidth = srcWidth / cols;
		} else {
			destWidth = (int) Math.floor(srcWidth / cols) + 1;
		}
		if (srcHeight % rows == 0) {
			destHeight = srcHeight / rows;
		} else {
			destHeight = (int) Math.floor(srcWidth / rows) + 1;
		}
		// 循环建立切片
		// 改进的想法:是否可用多线程加快切割速度
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// 四个参数分别为图像起点坐标和宽高
				// 即: CropImageFilter(int x,int y,int width,int height)
				cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
				result.add(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter)));
			}
		}
		return result;
	}

	/**
	 * 图像切割（指定切片的宽度和高度）
	 * 
	 * @param image
	 *            源图像
	 * @param destWidth
	 *            目标切片宽度。默认200
	 * @param destHeight
	 *            目标切片高度。默认150
	 * @return List
	 */
	public static List<Image> cut3(Image image, int destWidth, int destHeight) {
		List<Image> result = new ArrayList<Image>();
		int srcWidth = image.getWidth(null);
		int srcHeight = image.getHeight(null);
		if (destWidth <= 0 || destHeight <= 0) {
			return result;
		}
		// 读取源图像
		if (srcWidth > destWidth && srcHeight > destHeight) {
			ImageFilter cropFilter;
			int cols = 0; // 切片横向数量
			int rows = 0; // 切片纵向数量
			// 计算切片的横向和纵向数量
			if (srcWidth % destWidth == 0) {
				cols = srcWidth / destWidth;
			} else {
				cols = (int) Math.floor(srcWidth / destWidth) + 1;
			}
			if (srcHeight % destHeight == 0) {
				rows = srcHeight / destHeight;
			} else {
				rows = (int) Math.floor(srcHeight / destHeight) + 1;
			}
			// 循环建立切片
			// 改进的想法:是否可用多线程加快切割速度
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					// 四个参数分别为图像起点坐标和宽高
					// 即: CropImageFilter(int x,int y,int width,int height)
					cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
					result.add(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter)));
				}
			}
		}
		return result;
	}

	/**
	 * 给图片添加文字水印
	 * 
	 * @param image
	 *            源图像
	 * @param pressText
	 *            水印文字
	 * @param fontName
	 *            水印的字体名称
	 * @param fontStyle
	 *            水印的字体样式
	 * @param color
	 *            水印的字体颜色
	 * @param fontSize
	 *            水印的字体大小
	 * @param x
	 *            右边距
	 * @param y
	 *            下边距
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @param translucent
	 *            透明背景
	 * @return BufferedImage
	 */
	public static BufferedImage pressText(Image image, String pressText, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha,
			boolean translucent) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		if (translucent) {
			bi = g.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
			g.dispose();
			g = bi.createGraphics();
		}
		g.drawImage(image, 0, 0, null);
		g.setColor(color);
		Font font = new Font(fontName, fontStyle, fontSize);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 在指定坐标绘制水印文字
		g.drawString(pressText, w - x - fm.stringWidth(pressText), h - y - 1);
		g.dispose();
		return bi;
	}

	public static final int CORNER_LEFT_TOP = 1;
	public static final int CORNER_RIGHT_TOP = 2;
	public static final int CORNER_LEFT_BOTTOM = 3;
	public static final int CORNER_RIGHT_BOTTOM = 4;
	public static final int CORNER_CENTER = 0;

	/**
	 * 给图片添加图片水印
	 * 
	 * @param image
	 *            源图像
	 * @param over
	 *            水印图片
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @param corner
	 *            水印位置：0=居中，1=左上，2=右上，3=左下，4=右下
	 * @param translucent
	 *            透明背景
	 * @return BufferedImage
	 */
	public static BufferedImage pressImage(Image image, Image over, float alpha, int corner, boolean translucent) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		if (translucent) {
			bi = g.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
			g.dispose();
			g = bi.createGraphics();
		}
		g.drawImage(image, 0, 0, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 在指定坐标绘制水印文字
		int w2 = over.getWidth(null);
		int h2 = over.getHeight(null);
		if (corner == CORNER_LEFT_TOP) {
			g.drawImage(over, 0, 0, w2, h2, null);
		} else if (corner == CORNER_RIGHT_TOP) {
			g.drawImage(over, (w - w2), 0, w2, h2, null);
		} else if (corner == CORNER_LEFT_BOTTOM) {
			g.drawImage(over, 0, (h - h2), w2, h2, null);
		} else if (corner == CORNER_RIGHT_BOTTOM) {
			g.drawImage(over, (w - w2), (h - h2), w2, h2, null);
		} else {
			g.drawImage(over, (w - w2) / 2, (h - h2) / 2, w2, h2, null);
		}
		g.dispose();
		return bi;
	}
	
	public static String image2Base64(BufferedImage bufferedImage, String formatName) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, formatName, baos);
		byte[] data = baos.toByteArray();
		return Base64.getEncoder().encodeToString(data);
	}
	
	public static BufferedImage base642Image(String base64String) throws IOException {
		byte[] data = Base64.getDecoder().decode(base64String);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		return ImageIO.read(bais);
	}
}
