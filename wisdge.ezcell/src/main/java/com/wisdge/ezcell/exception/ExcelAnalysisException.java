package com.wisdge.ezcell.exception;

public class ExcelAnalysisException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExcelAnalysisException() {
	}

	public ExcelAnalysisException(String message) {
		super(message);
	}

	public ExcelAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcelAnalysisException(Throwable cause) {
		super(cause);
	}
}
