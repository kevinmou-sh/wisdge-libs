package com.wisdge.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.util.Base64Utils;

/**
 * MD5 encrypt algorithm. <br>
 * Encryption irreversible.
 */
public final class MD5 {
	/** Determine encrypt algorithm MD5 */
	private static final String ALGORITHM_MD5 = "MD5";
	private static final Log logger = LogFactory.getLog(MD5.class);

	/**
	 * MD5 16bit Encrypt Methods.
	 * 
	 * @param readyEncryptStr
	 *            ready encrypt string
	 * @return String encrypt result string
	 * @throws NoSuchAlgorithmException
	 */
	public static final String encrypt16(String readyEncryptStr) {
		if (readyEncryptStr != null) {
			String encrypt = MD5.encrypt32(readyEncryptStr);
			return encrypt == null? null : encrypt.substring(8, 24);
		} else {
			return null;
		}
	}

	/**
	 * MD5 32bit Encrypt Methods.
	 * 
	 * @param data
	 *            ready encrypt string
	 * @return String encrypt result string
	 */
	public static final String encrypt32(String data) {
		try {
			// Get MD5 digest algorithm's MessageDigest's instance.
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			// Use specified byte update digest.
			md.update(data.getBytes());
			// Get cipher text
			byte[] b = md.digest();
			// The cipher text converted to hexadecimal string
			StringBuilder su = new StringBuilder();
			// byte array switch hexadecimal number.
			for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
				String haxHex = Integer.toHexString(b[offset] & 0xFF);
				if (haxHex.length() < 2) {
					su.append("0");
				}
				su.append(haxHex);
			}
			return su.toString();
		} catch(Exception e) {
			logger.error(e, e);
			return null;
		}
	}

	/**
	 * MD5 32bit Encrypt Methods.
	 * 
	 * @param data
	 *            ready encrypt string
	 * @return String encrypt result string
	 */
	public static final String encrypt32_2(String data) {
		try {
			// The cipher text converted to hexadecimal string
			StringBuilder su = new StringBuilder();
			// Get MD5 digest algorithm's MessageDigest's instance.
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			byte[] b = md.digest(data.getBytes());
			int temp = 0;
			// byte array switch hexadecimal number.
			for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
				temp = b[offset];
				if (temp < 0) {
					temp += 256;
				}
				int d1 = temp / 16;
				int d2 = temp % 16;
				su.append(Integer.toHexString(d1) + Integer.toHexString(d2));
			}
			return su.toString();
		} catch(Exception e) {
			logger.error(e, e);
			return null;
		}
	}

	/**
	 * MD5 32bit Encrypt Methods.
	 * 
	 * @param data
	 *            ready encrypt string
	 * @return String encrypt result string
	 */
	public static final String encrypt32_3(String data) {
		try {
			// The cipher text converted to hexadecimal string
			StringBuilder su = new StringBuilder();
			// Get MD5 digest algorithm's MessageDigest's instance.
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			// Use specified byte update digest.
			md.update(data.getBytes());
			byte[] b = md.digest();
			int temp = 0;
			// byte array switch hexadecimal number.
			for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
				temp = b[offset];
				if (temp < 0) {
					temp += 256;
				}
				if (temp < 16) {
					su.append("0");
				}
				su.append(Integer.toHexString(temp));
			}
			return su.toString();
		} catch(Exception e) {
			logger.error(e, e);
			return null;
		}
	}

	/**
	 * MD5 16bit Encrypt Methods.
	 * 
	 * @param data
	 *            ready encrypt string
	 * @return String encrypt result string
	 */
	public static final String encrypt64(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
			return Base64Utils.encodeToString(md.digest(data.getBytes(StandardCharsets.UTF_8)));
		} catch(Exception e) {
			logger.error(e, e);
			return null;
		}
	}
	
	public static final String encrypt(String message) {
		return org.apache.commons.codec.digest.DigestUtils.md5Hex(message);
	}
	
	private static final String HMAC_MD5 = "HmacMD5";
	public static String hmac(String message, String key) {
		try {
			Mac mac = Mac.getInstance(HMAC_MD5);
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), HMAC_MD5);
			mac.init(secretKey);
			return toHexString(mac.doFinal(message.getBytes()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	public static final byte[] encrypt(byte[] data, String key) {
		try {
			Mac mac = Mac.getInstance(HMAC_MD5);
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), HMAC_MD5);
			mac.init(secretKey);
			return mac.doFinal(data);
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
	public void test() {
		String message = "simida😈中国";
		String key = "letmein0308";
		try {
			String md5_16 = MD5.encrypt16(message);
			System.out.println("16bit-md5:" + md5_16);
			String md5_32 = MD5.encrypt32(message);
			String md5_32_2 = MD5.encrypt32_2(message);
			String md5_32_3 = MD5.encrypt32_3(message);
			System.out.println("32bit-md5:");
			System.out.println("\t1:  " + md5_32);
			System.out.println("\t2:  " + md5_32_2);
			System.out.println("\t3:  " + md5_32_3);
			String md5_64 = MD5.encrypt64(message);
			System.out.println("64bit-md5:" + md5_64); //
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("md5:" + MD5.encrypt(message));
		System.out.println("md5-hmac:" + MD5.hmac(message, key));
	}

}
