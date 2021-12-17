package com.wisdge.commons.interfaces;

public interface PdfConverter {
	byte[] convert(byte[] data) throws Exception;
	byte[] convert(String source) throws Exception;
}
