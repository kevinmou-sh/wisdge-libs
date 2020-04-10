package com.wisdge.utils.security;

public interface Decryptor {
	/**
	 * 加密字符串
	 * 
	 * @param original
	 *            String 用户名
	 * @return 密文
	 */
	public String encrypt(String original);
}
