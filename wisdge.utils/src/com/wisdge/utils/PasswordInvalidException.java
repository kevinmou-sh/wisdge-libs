package com.wisdge.utils;

public class PasswordInvalidException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int code;
	
	public PasswordInvalidException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	
}
