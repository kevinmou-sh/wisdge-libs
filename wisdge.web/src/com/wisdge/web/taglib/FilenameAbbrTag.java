package com.wisdge.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.io.FilenameUtils;
import com.wisdge.utils.StringUtils;
import com.wisdge.web.HtmlUtils;

/**
 * 将文件名的后缀去除，根据约定的长度进行缩略。
 * 
 * @author Kevin.MOU
 */
public class FilenameAbbrTag extends TagSupport {
	private static final long serialVersionUID = 453826675205694726L;
	private static final String ATTRIBUTE_ERROR = "Cann't find filename attributes.";
	private String filename;
	private int size;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if (filename == null)
				pageContext.getOut().println(ATTRIBUTE_ERROR);
			else {
				String extension = FilenameUtils.getExtension(filename);
				if (extension != null && extension.length() > 0)
					extension = "." + extension;
				filename = StringUtils.stripTrailing(filename, extension);
				filename = StringUtils.abbreviate(filename, size);
				filename = HtmlUtils.htmlEscape(filename);
				pageContext.getOut().print(filename + extension);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("FilenameAbbrTag: " + e.getMessage());
		}

		return super.doEndTag();
	}

}
