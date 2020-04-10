package com.wisdge.web.taglib;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.io.FilenameUtils;

public class ContentTypeTag extends TagSupport {
	private static final long serialVersionUID = -495389874447418421L;
	private String filename;
	private boolean imgShow;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isImgShow() {
		return imgShow;
	}

	public void setImgShow(boolean imgShow) {
		this.imgShow = imgShow;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if (filename==null)
				pageContext.getOut().println("No filename defined.");
			else {
				String contentType = FileTypeMap.getDefaultFileTypeMap().getContentType(filename);
				if (imgShow) {
					String extension = FilenameUtils.getExtension(filename);
					HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
					String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
					if (basePath.endsWith("/"))
						basePath.substring(0, basePath.length()-1);
					String imgURL = " <img src='"+basePath+"/feImg?ext="+extension+"' align='absmiddle'/>";
					contentType += " "+imgURL;
				}
				pageContext.getOut().print(contentType);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("ContentTypeTag: " + e.getMessage());
		}
		return super.doEndTag();
	}

}
