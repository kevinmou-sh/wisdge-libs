package com.wisdge.utils.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES算法基础类，计算结果为字符串数组
 *
 * @author Kevin MOU
 */
public class DES {
	public static SecretKey initSecretKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return initSecretKey(password.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 初始化秘钥
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static SecretKey initSecretKey(byte[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		KeyGenerator kgen = KeyGenerator.getInstance("DES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
        secureRandom.setSeed(password);
		kgen.init(secureRandom);
		return kgen.generateKey();
	}

    /**
     * Description 根据键值进行加密
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bt);
    }

    /**
     * Description 根据键值进行加密
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
    	// 本方法与crypto-js兼容
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DES"));
        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     */
    public static String decrypt(String data, String key) throws IOException, Exception {
        if (data == null) return null;
        byte[] bt = decrypt(Base64.getDecoder().decode(data), key.getBytes(StandardCharsets.UTF_8));
        return new String(bt, StandardCharsets.UTF_8);
    }

    /**
     * Description 根据键值进行解密
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
    	// 本方法与crypto-js兼容
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "DES"));
        return cipher.doFinal(data);
    }
}
