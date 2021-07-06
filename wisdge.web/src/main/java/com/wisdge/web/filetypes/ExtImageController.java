package com.wisdge.web.filetypes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wisdge.web.springframework.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExtImageController {

    @GetMapping("/file-ext-image")
    public void fileExtImage(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        String ext = WebUtils.getString(request, "ext", "");
        byte[] data = FileExt.getImgByExt(ext);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType(FileExt.getContentTypeByExt(ext));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.flushBuffer();
    }
}
