/**
 * Description: Web download file class
 * Copyright:(c)2011 Wisdge.com
 * @author Kevin MOU
 * @version 1.5
 */
package com.wisdge.web.filetypes;

import java.io.InputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

/**
 * 展示文件类型16像素图标的Servlet
 * 
 * <pre>
 * Examples: 
 * add following content in web.xml
 * &ltservlet&gt
 * 	&ltservlet-name&gtFileExtImageDispatcher&lt/servlet-name&gt
 * 	&ltservlet-class&gtcom.wisdge.web.filetypes.ImgDispatcher&lt/servlet-class&gt
 * &lt/servlet&gt
 * &ltservlet-mapping&gt
 * 	&ltservlet-name&gtFileExtImageDispatcher&lt/servlet-name&gt
 * 	&lturl-pattern&gt/feImg&lt/url-pattern&gt
 * &lt/servlet-mapping>
 * </pre>
 * 
 * <pre>
 * HTML show file image:  &ltimg src="/feImg?ext=jpg" /&gt
 * </pre>
 * 
 * @author Kevin MOU
 */
public class ImgDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {

		String ext = request.getParameter("ext");
		String fileImg = FileExt.getImgByExt(ext);
		// System.out.println("extension file: "+ fileImg);
		InputStream in = this.getClass().getResourceAsStream("/com/wisdge/web/filetypes/imgs/" + fileImg);
		try {
			byte[] bmp = IOUtils.toByteArray(in);
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.getOutputStream().write(bmp);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
