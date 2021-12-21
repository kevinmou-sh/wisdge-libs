package com.wisdge.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 该JavaBean可以直接在其他Java应用程序中调用，实现屏幕的"拍照"。<br>
 * 默认的文件前缀为GuiCamera，文件格式为PNG格式 <br>
 *
 * @author Kevin MOU
 */
public class SnapScreen {
	public static final String FORMAT_PNG = "png";
	private static final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * 对屏幕进行拍照，写入文件
	 */
	public static void shot(String filePath, String format) throws AWTException, IOException {
		// 拷贝屏幕到一个BufferedImage对象screenshot
		BufferedImage screenshot = (new Robot()).createScreenCapture(new Rectangle(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight()));
		// 根据文件前缀变量和文件格式变量，自动生成文件名
		String name = filePath + "." + format;
		File file = new File(name);
		// 将screenshot对象写入图像文件
		ImageIO.write(screenshot, format, file);
	}

	/**
	 * 对屏幕进行拍照，且输出到字节对象
	 */
	public static byte[] shotToBytes() throws AWTException, IOException {
		// 拷贝屏幕到一个BufferedImage对象screenshot
		BufferedImage screenshot = (new Robot()).createScreenCapture(new Rectangle(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight()));
		// 将screenshot对象写入字节对象
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(screenshot, FORMAT_PNG, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}
}
