package com.wisdge.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import com.wisdge.web.HtmlUtils;

/**
 * 进行HTML转义
 * 
 * @author Kevin.MOU
 */
public class HtmlEncoderTag implements Tag {
	private static final String VALUE_ERROR = "Cann't find value or size attributes.";
	private PageContext pageContext;
	private String value;
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

	public void setPageContext(PageContext arg0) {
		this.pageContext = arg0;
	}

	public void setParent(Tag arg0) {
		// TODO Auto-generated method stub

	}

	public Tag getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int doEndTag() throws JspException {
		try {
			if (value == null)
				pageContext.getOut().println(VALUE_ERROR);
			else {
				String str;
				if (advance == null || advance == true)
					str = HtmlUtils.htmlEscapeEx(value);
				else
					str = HtmlUtils.htmlEscape(value);
				pageContext.getOut().print(str);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("HtmlEncoderTag: " + e.getMessage());
		}
		return SKIP_BODY;
	}

	public void release() {
		// TODO Auto-generated method stub

	}
}
