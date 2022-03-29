package com.wisdge.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class SHA {
	private static final Log logger = LogFactory.getLog(SHA.class);
	public static final String ENCTYPE_256 = "SHA-256";
	public static final String ENCTYPE_512 = "SHA-512";

	public static String encrypt(String message) {
		try {
			return encrypt(message, ENCTYPE_256);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
	
	public static String encrypt512(String message) {
		try {
			return encrypt(message, ENCTYPE_512);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}

	/**
	 * 进行SHA签名
	 * 
	 * @param message	String 签名对象
	 * @param encName	String 签名方式：ENCTYPE_256/ENCTYPE_512
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String encrypt(String message, String encName) throws NoSuchAlgorithmException {
		byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance(encName);
		md.update(bytes);
		return toHexString(md.digest());
	}

	/**
	 * 得到签名
	 * 
	 * @param data
	 * @param encName
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] encrypt(byte[] data, String encName) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(encName);
		md.update(data);
		return md.digest();
	}
	
	private static final String HMAC_SHA256 = "HmacSHA256";
	public static String hmac(String message, String secret) {
		try {
			Mac sha256HMAC = Mac.getInstance(HMAC_SHA256);
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
			sha256HMAC.init(secretKey);
			return toHexString(sha256HMAC.doFinal(message.getBytes()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}
	
	public static byte[] hmac(byte[] data, String secret) {
		try {
			Mac sha256HMAC = Mac.getInstance(HMAC_SHA256);
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
			sha256HMAC.init(secretKey);
			return sha256HMAC.doFinal(data);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	/**
	 * 转16进制
	 * 
	 * @param origBytes
	 * @return
	 */
	public static String toHexString(byte[] origBytes) {
		String tempStr = null;
		StringBuilder stb = new StringBuilder();
		for (int i = 0; i < origBytes.length; i++) {
			tempStr = Integer.toHexString(origBytes[i] & 0xFF);
			if (tempStr.length() == 1) {
				stb.append("0");
			}
			stb.append(tempStr);
		}
		return stb.toString();
	}
	
	@Test
	public void test() throws NoSuchAlgorithmException, InvalidKeyException {
		String message = "simida😈中国";
		String key = "letmein0308";
		System.out.println("SHA256: " + SHA.encrypt(message));
		System.out.println("HmacSHA256: " + SHA.hmac(message, key));
	}
}
