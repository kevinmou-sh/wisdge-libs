package com.wisdge.dataservice.exceptions;

public class IllegalUrlException extends Exception {
	private static final long serialVersionUID = 4908073448269704323L;
	private int code;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public IllegalUrlException(int code, String message) {
		super(message);
		this.code = code;
	}

}
