package com.wisdge.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import com.wisdge.utils.StringUtils;
import com.wisdge.web.HtmlUtils;

/**
 * 对多行文字进行缩略
 * 
 * @author Kevin.MOU
 */
public class MutilAbbrTag implements Tag {

	private static final String VALUE_ERROR = "Cann't find value or size attributes.";
	private PageContext pageContext;
	private String value;
	private int size = 30;
	private int rows = 1;
	private Boolean advance;

	public Boolean getAdvance() {
		return advance;
	}

	public void setAdvance(Boolean advance) {
		this.advance = advance;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setPageContext(PageContext arg0) {
		this.pageContext = arg0;
	}

	public void setParent(Tag arg0) {

	}

	public Tag getParent() {
		return null;
	}

	public int doStartTag() throws JspException {
		return 0;
	}

	public int doEndTag() throws JspException {
		try {
			if (value == null)
				pageContext.getOut().println(VALUE_ERROR);
			else {
				if (advance == null)
					advance = true;

				// set max size
				value = StringUtils.abbreviate(value, size);

				// set mutil line
				int enter = 0;
				for (int i = 0; i < rows; i++) {
					enter = value.indexOf('\n', enter);
					if (enter == -1)
						break;
					enter++;
				}
				if (enter != -1)
					value = value.substring(0, enter - 1).concat("...");

				// transfer html encoding
				String str;
				if (advance == null || advance == true)
					str = HtmlUtils.htmlEscapeEx(value);
				else
					str = HtmlUtils.htmlEscape(value);
				pageContext.getOut().print(str);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("MutilAbbrTag: " + e.getMessage());
		}
		return SKIP_BODY;
	}

	public void release() {

	}
}
