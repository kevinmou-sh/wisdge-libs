package com.wisdge.utils.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.junit.Test;

/**
 * DES算法基础类，计算结果为字符串数组，应用中调用DESPlus
 * 
 * @author Kevin MOU
 * @see DESPlus
 */
public class DES {
    private final static String ENCODE = "UTF-8";

	public static SecretKey initSecretKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return initSecretKey(password.getBytes(ENCODE));
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
        byte[] bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
        return byte2hex(bt);
    }

    /**
     * Description 根据键值进行加密
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
    	/*
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, initSecretKey(key), new SecureRandom());
        return cipher.doFinal(data);
        */
    	
    	// 本方法与crypto-js兼容
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     */
    public static String decrypt(String data, String key) throws IOException, Exception {
        if (data == null)
            return null;
        byte[] bt = decrypt(hex2byte(data.getBytes(ENCODE)), key.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行解密
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
    	/*
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, initSecretKey(key), new SecureRandom());
        return cipher.doFinal(data);
        */
        
    	// 本方法与crypto-js兼容
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
        return cipher.doFinal(data);
    }
    
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer("");
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString();
	}

	public static byte[] hex2byte(byte[] b) {
		if (b.length % 2 != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[(n / 2)] = ((byte) Integer.parseInt(item, 16));
		}
		return b2;
	}
   
	@Test
	public void test() throws Exception {
		String str = "测试内容";
		String password = "elite.ngs";
		String result = DES.encrypt(str, password);
		System.out.println("加密后内容为：" + result);
		System.out.println("解密后内容为：" + DES.decrypt(result, password));
	}

}
