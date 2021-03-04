package com.wisdge.utils;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * 该JavaBean可以直接在其他Java应用程序中调用，实现屏幕的"拍照"。<br>
 * 默认的文件前缀为GuiCamera，文件格式为PNG格式 <br>
 * The default construct will use the default Image file surname "SnapScreen", and default image format "png"
 * 
 * @author Kevin MOU
 */
public class SnapScreen {
	private String fileName; // 文件的前缀
	private String defaultName = "SnapScreen";
	static int serialNum = 0;
	private String imageFormat; // 图像文件的格式
	private String defaultImageFormat = "png";
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

	public SnapScreen() {
		fileName = defaultName;
		imageFormat = defaultImageFormat;

	}

	/**
	 * @param s
	 *            the surname of the snapshot file
	 * @param format
	 *            the format of the image file, it can be "jpg" or "png" 本构造支持JPG和PNG文件的存储
	 */
	public SnapScreen(String s, String format) {

		fileName = s;
		imageFormat = format;
	}

	/**
	 * 对屏幕进行拍照 snapShot the Gui once
	 */
	public void snapShot() {
		try {
			// 拷贝屏幕到一个BufferedImage对象screenshot
			BufferedImage screenshot = (new Robot()).createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
			serialNum++;
			// 根据文件前缀变量和文件格式变量，自动生成文件名
			String name = fileName + String.valueOf(serialNum) + "." + imageFormat;
			File f = new File(name);
			System.out.print("Save File " + name);
			// 将screenshot对象写入图像文件
			ImageIO.write(screenshot, imageFormat, f);
			System.out.print("..Finished!\n");
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public static void main(String[] args) {
		SnapScreen cam = new SnapScreen("d:\\", "png");
		cam.snapShot();
	}
}
