package com.wisdge.dataservice.exceptions;

import org.apache.commons.lang3.StringUtils;

public class XhrException extends Exception {
	private static final long serialVersionUID = 4908073448269704324L;
	private int code;
	private String payload;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public XhrException(int code, String reason, String payload) {
		super(StringUtils.isEmpty(reason) ? ("Request failed [" + code + "]") : reason);
		this.code = code;
		this.payload = payload;
	}

}
