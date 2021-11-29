package com.wisdge.utils.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

/**
 * AES加密，与Nodejs 保持一致
 */
public class AESForNodejs {
	public static final String DEFAULT_CODING = "utf-8";

	/**
	 * 解密
	 *
	 * @param encrypted
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypted, String key) throws Exception {
		byte[] keyb = key.getBytes(DEFAULT_CODING);
		SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
		Cipher dcipher = Cipher.getInstance("AES");
		dcipher.init(Cipher.DECRYPT_MODE, skey);
		byte[] clearbyte = dcipher.doFinal(toByte(encrypted));
		return new String(clearbyte);
	}

	/**
	 * 加密
	 *
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content, String key) throws Exception {
		byte[] enCodeFormat = key.getBytes("utf-8");
		SecretKeySpec akey = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] byteContent = content.getBytes("utf-8");
		cipher.init(Cipher.ENCRYPT_MODE, akey);
		byte[] result = cipher.doFinal(byteContent);
		return parseByte2HexStr(result);
	}

	/**
	 * 字符串转字节数组
	 *
	 * @param hexString
	 * @return
	 */
	private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
		}
		return result;
	}

	/**
	 * 字节转16进制数组
	 *
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
