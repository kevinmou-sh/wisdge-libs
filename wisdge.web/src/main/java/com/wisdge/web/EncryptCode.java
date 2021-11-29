/**
 * Description: Encode digit picture generator
 * Copyright(c)2011 Wisdge.com
 * @author Kevin MOU
 * @version 1.1
 */
package com.wisdge.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在网页中使用验证码的类<br />
 *
 * HTML page example: http://www.example.com/encImg?style=digit&length=6&ecsid=TESTID
 */
@WebServlet(displayName = "VerifyCode", name = "VerifyCode", urlPatterns = { "/verifyCode" }, loadOnStartup = 1)
public class EncryptCode extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CHOOSE_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CHOOSE_CHAR_LOWCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String CHOOSE_CHAR_UPCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int defaultLength = 4;
	private static final int defaultFontSize = 18;
	public static final String SESSION_ATTRIBUTE = "Wisdge_EncryptCode_1.1";

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
		String style = request.getParameter("style");
		if (style == null) {
			style = "mix";
		}
		// get length of image encoder
		int length = defaultLength;
		String strLength = request.getParameter("length");
		try {
			length = Integer.parseInt(strLength);
		} catch (NumberFormatException e) {
		}
		String ecsid = request.getParameter("ecsid");
		String encryptCode = getStringRandom(style, length);

		int fontSize = defaultFontSize;
		String strFontSize = request.getParameter("fs");
		try {
			fontSize = Integer.parseInt(strFontSize);
		} catch (NumberFormatException e) {
		}

		Font font = new Font("Times New Roman", Font.PLAIN, fontSize);
		BufferedImage tmpImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics tmpG = tmpImage.getGraphics();
		tmpG.drawString(encryptCode, 0, 100);
		FontMetrics metrics = tmpG.getFontMetrics(font);
		int width = metrics.stringWidth(encryptCode) + 12;
		int height = metrics.getMaxDescent() + metrics.getMaxAscent();
		int baseline = metrics.getMaxAscent();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.fillRect(0, 0, width, height);
		g.setFont(font);
		g.setColor(getRandColor(160, 200));

		Random random = new Random();
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		String pad = "";
		for (int i = 0; i < length; i++) {
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			String s = encryptCode.substring(i, i + 1);
			rotateText(g, s, getRandRotate(), metrics.stringWidth(pad) + 6, baseline);
			pad += s;
		}
		g.dispose();
		tmpG.dispose();
		request.getSession().setAttribute(generatECSID(ecsid), encryptCode);

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}

	private double getRandRotate() {
		Random random = new Random();
		return random.nextInt(30) * (random.nextBoolean() ? 1: -1);
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);

		return new Color(r, g, b);
	}

	private String getStringRandom(String style, int length) {
		Random random = new Random();
		StringBuffer randBuf = new StringBuffer();
		;
		for (int i = 0; i < length; i++) {
			String rand;
			if (style.equals("digit")) {
				rand = String.valueOf(random.nextInt(10));
			} else if (style.equals("char")) {
				rand = String.valueOf(CHOOSE_CHAR.charAt(random.nextInt(CHOOSE_CHAR.length())));
			} else if (style.equals("lowcase")) {
				rand = String.valueOf(CHOOSE_CHAR_LOWCASE.charAt(random.nextInt(CHOOSE_CHAR_LOWCASE.length())));
			} else if (style.equals("upcase")) {
				rand = String.valueOf(CHOOSE_CHAR_UPCASE.charAt(random.nextInt(CHOOSE_CHAR_UPCASE.length())));
			} else {
				if (i % 2 == 0) {
					rand = String.valueOf(random.nextInt(10));
				} else {
					rand = String.valueOf(CHOOSE_CHAR.charAt(random.nextInt(CHOOSE_CHAR.length())));
				}
			}
			randBuf.append(rand);
		}
		return randBuf.toString();
	}

	public static String generatECSID(String ecsid) {
		if (ecsid == null) {
			return SESSION_ATTRIBUTE;
		}

		return SESSION_ATTRIBUTE + "_" + ecsid;
	}

	public static boolean matchCode(HttpServletRequest request, String code) {
		return matchCode(request, code, null);
	}

	public static boolean matchCode(HttpServletRequest request, String code, String ecsid) {
		String vcode = (String) request.getSession().getAttribute(generatECSID(ecsid));
		return (vcode.equals(code));
	}

	public void rotateText(Graphics g, String s, double angle, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;

		// Move the origin to the (x,y)
		g2d.translate(x, y);
		// Rotate the angle
		g2d.rotate(Math.PI * (angle / -180));
		// Draw text
		g2d.drawString(s, 0, 0);
		// Restore moving and rotating
		g2d.rotate(-Math.PI * (angle / -180));
		g2d.translate(-x, -y);
	}

}
