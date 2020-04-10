package com.wisdge.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import com.wisdge.utils.StringUtils;
import com.wisdge.web.HtmlUtils;

/**
 * 扩展标签，用于字符串缩略
 * 
 * @author Kevin MOU
 * @version 1.2
 */
public class AbbrTag implements Tag {
	private static final String ATTRIBUTE_ERROR = "Cann't find value or size attributes.";
	private PageContext pageContext;
	private String value;
	private Integer size;
	private Boolean htmlencode;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
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

	public Boolean getHtmlencode() {
		return htmlencode;
	}

	public void setHtmlencode(Boolean htmlencode) {
		this.htmlencode = htmlencode;
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
			if (value == null || size == null)
				pageContext.getOut().println(ATTRIBUTE_ERROR);
			else {
				value = StringUtils.abbreviate(value, size);
				if (htmlencode != null && htmlencode.booleanValue())
					value = HtmlUtils.htmlEscape(value);
				pageContext.getOut().print(value);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("AbbrTag: " + e.getMessage());
		}
		return SKIP_BODY;
	}

	public void release() {
		// TODO Auto-generated method stub

	}
}
