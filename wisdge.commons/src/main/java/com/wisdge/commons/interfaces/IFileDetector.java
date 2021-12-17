package com.wisdge.commons.interfaces;

import java.io.InputStream;

public interface IFileDetector {
	boolean isSafe(InputStream inputStream) throws Exception;
	boolean isSafe(byte[] data) throws Exception;
}
