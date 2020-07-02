/**
 * Description: Web download file class
 * Copyright:(c)2011 Wisdge.com
 * @author Kevin MOU
 * @version 1.5
 */
package com.wisdge.web.filetypes;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisdge.web.springframework.WebUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExtImageController {
	private static final long serialVersionUID = 1L;

	@GetMapping("/extImage")
	public void extImage(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
		String ext = WebUtils.getString(request, "ext", "");
		String fileImg = FileExt.getImgByExt(ext);
		try (InputStream in = this.getClass().getResourceAsStream("/com/wisdge/web/filetypes/imgs/" + fileImg)) {
			byte[] bmp = IOUtils.toByteArray(in);
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.getOutputStream().write(bmp);
		}
	}
}
