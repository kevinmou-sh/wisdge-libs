package com.wisdge.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 从目标URL获得指定尺寸大小图片的Servlet<br/>
 * Examples: 
 * <pre>
 * add following content in web.xml
 * &ltservlet&gt
 * 	&ltservlet-name&gtFitImageDispatcher&lt/servlet-name&gt
 * 	&ltservlet-class&gtcom.wisdge.web.FitImage&lt/servlet-class&gt
 * &lt/servlet&gt
 * &ltservlet-mapping&gt
 * 	&ltservlet-name&gtFitImageDispatcher&lt/servlet-name&gt
 * 	&lturl-pattern&gt/fitImg&lt/url-pattern&gt
 * &lt/servlet-mapping>
 * </pre>
 * 
 * <pre>
 * HTML show file image:  &ltimg src="http://example.com/fitImg?pic=images/source.jpg&w=100&h=100" /&gt
 * </pre>
 * 
 * @author Kevin MOU
 * @version 1.0.20121130
 */

public class FitImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Font mFont = new Font("Verdana", Font.CENTER_BASELINE, 12);
	private static final int fsw = 80;
	private static final int fsh = 0;

	// Initialize global variables
	public void init() throws ServletException {
	}

	// Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String img = request.getParameter("pic");
		if (img == null)
			return;

		int w = getIntFromString(request.getParameter("w"), 60);
		int h = getIntFromString(request.getParameter("h"), 40);
		// System.out.println("["+DateUtil.getSimpleTime(new Date())+" ]fix
		// image="+img+" width="+w+" height="+h);

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		ServletOutputStream out = response.getOutputStream();
		File imgfile = new File(this.getServletContext().getRealPath(img));
		if (imgfile.exists()) {
			BufferedImage a = ImageIO.read(imgfile);
			response.setContentType("image/jpeg");
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics gra = image.getGraphics();
			gra.setColor(Color.white);
			gra.fillRect(0, 0, w, h);
			drawFixImage(gra, a, w, h);
			try {
				ImageIO.write(image, "jpeg", out);
			} catch (Exception e) {
				System.err.println("JPEG encoder faild. " + e.getMessage());
			}
		} else {
			response.setContentType("image/jpeg");
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics gra = image.getGraphics();
			gra.setColor(new Color(200, 200, 200));
			gra.fillRect(1, 1, w - 1, h - 1);
			gra.setColor(Color.white);
			gra.setFont(mFont);
			gra.drawString("No Picture", (w - fsw) / 2, (h - fsh) / 2);
			try {
				ImageIO.write(image, "jpeg", out);
			} catch (Exception e) {
				System.out.println("[ERROR]FitImage: " + e.getMessage());
			}
		}
		out.close();
	}

	public static void drawFixImage(Graphics g, Image image, int unitW, int unitH) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		int cw, ch; // fixed size

		// 判断原图长宽比例
		if ((float) w / h > (float) unitW / unitH) { // // 原图长宽比例中, 宽度为优
			if (w > unitW)
				cw = unitW;
			else
				cw = w;
			ch = (int) ((float) h / w * cw);
		} else { // 原图长宽比例中，高度为优
			if (h > unitH)
				ch = unitH;
			else
				ch = h;
			cw = (int) ((float) w / h * ch);
		}
		int startx = (unitW - cw) / 2;
		int starty = (unitH - ch) / 2;
		g.drawImage(image, startx, starty, cw + startx, ch + starty, 0, 0, w, h, null);
	}

	// Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	// Clean up resources
	public void destroy() {
	}

	private int getIntFromString(String str, int defaultValue) {
		int result = defaultValue;
		try {
			result = Integer.parseInt(str);
		} catch (NumberFormatException e) {

		}
		return result;
	}
}
