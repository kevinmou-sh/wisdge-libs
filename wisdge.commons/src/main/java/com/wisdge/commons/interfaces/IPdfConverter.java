package com.wisdge.commons.interfaces;

public interface IPdfConverter {
	byte[] convert(byte[] data, String dataType) throws Exception;
	byte[] convert(String source) throws Exception;
}
