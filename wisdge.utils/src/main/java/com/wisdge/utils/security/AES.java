package com.wisdge.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;

public class AES {
	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
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

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 */
	public static byte[] encryptToBytes(String content, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		return encryptToBytes(content, password, "ECB", "PKCS5Padding");
	}
	
	public static byte[] encryptToBytes(String content, String password, String mode, String padding) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		SecretKey secretKey = initKey(password);
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		
		// 与nodejs互通的关键在于key的处理上，本方法没有key的长度约定限制，nodejs方法key必须是固定长度 (8的倍数）
		// SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);
		byte[] byteContent = content.getBytes("UTF-8");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(byteContent);
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] decryptFromBytes(byte[] content, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return decryptFromBytes(content, password, "ECB", "PKCS5Padding");
	}
	
	public static byte[] decryptFromBytes(byte[] content, String password, String mode, String padding) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		SecretKey secretKey = initKey(password);
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		
		// 与nodejs互通的关键在于key的处理上，本方法没有key的长度约定限制，nodejs方法key必须是固定长度 (8的倍数）
		// SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(content);
	}

	public static SecretKey initKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 初始化秘钥
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
        secureRandom.setSeed(password.getBytes("UTF-8"));  
		kgen.init(128, secureRandom);
		return kgen.generateKey();
	}
	
	public static String encrypt(String content, String password) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		byte[] encryptResult = encryptToBytes(content, password);
		return parseByte2HexStr(encryptResult);
	}
	
	/**
	 * @param content
	 * @param password
	 * @param mode
	 * @param padding
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchPaddingException
	 */
	public static String encrypt(String content, String password, String mode, String padding) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		byte[] encryptResult = encryptToBytes(content, password, mode, padding);
		return parseByte2HexStr(encryptResult);
	}

	/**
	 * @param content
	 * @param password
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException 
	 */
	public static String decrypt(String content, String password) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] decryptFrom = parseHexStr2Byte(content);
		byte[] decryptResult = decryptFromBytes(decryptFrom, password);
		return new String(decryptResult);
	}

	@Test
	public void test() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		String content = "test";
		String password = "BN1ZbZSxElVIhwpN";
		String aes = encrypt(content, password);
		System.out.println("加密后内容为：" + aes);
		System.out.println("解密后内容为：" + decrypt(aes, password));
	}
}