package com.wisdge.utils.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import org.junit.Test;

public class FileEncryptor {
	private Key key;
	public static final int AES_256KEY = 256;
	public static final int AES_128KEY = 128;

	public FileEncryptor(String strKey) {
		this(strKey, AES_128KEY);
	}

	public FileEncryptor(String strKey, int keyLength) {
		getKey(strKey, keyLength);// 生成密匙
	}

	/**
	 * 根据参数生成KEY
	 */
	public void getKey(String strKey, int keyLength) {
		try {
//			KeyGenerator _generator = KeyGenerator.getInstance("DES");
//			_generator.init(new SecureRandom(strKey.getBytes()));
//			this.key = _generator.generateKey();
//			_generator = null;
			// 初始化秘钥
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
	        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	        secureRandom.setSeed(strKey.getBytes());
			kgen.init(keyLength, secureRandom);
			this.key = kgen.generateKey();
		} catch (Exception e) {
			throw new RuntimeException("Error getKey, Cause: " + e);
		}
	}

	public byte[] encrypt(byte[] content) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
//		Cipher cipher = Cipher.getInstance("DES");
//		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CipherInputStream cis = new CipherInputStream(bais, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			baos.write(buffer, 0, r);
		}
		cis.close();
		bais.close();
		byte[] data = baos.toByteArray();
		baos.close();
		return data;
	}

	public byte[] decrypt(byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
//		Cipher cipher = Cipher.getInstance("DES");
//		cipher.init(Cipher.DECRYPT_MODE, this.key);
		ByteArrayInputStream bais = new ByteArrayInputStream(encrypted);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CipherOutputStream cos = new CipherOutputStream(baos, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = bais.read(buffer)) >= 0) {
			cos.write(buffer, 0, r);
		}
		cos.close();
		bais.close();
		byte[] data = baos.toByteArray();
		baos.close();
		return data;
	}

	public CipherOutputStream getCipherOutputStream(OutputStream os) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
		CipherOutputStream cos = new CipherOutputStream(os, cipher);
		return cos;
	}

	/**
	 * 文件file进行加密并保存目标文件destFile中
	 *
	 * @param file
	 *            要加密的文件 如c:/test/srcFile.txt
	 * @param destFile
	 *            加密后存放的文件名 如c:/加密后文件.txt
	 */
	public void encrypt(String file, String destFile) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
//		Cipher cipher = Cipher.getInstance("DES");
//		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(destFile);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.close();
	}

	public CipherInputStream getCipherInputStream(InputStream is) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		return cis;
	}

	/**
	 * 文件采用DES算法解密文件
	 *
	 * @param file
	 *            已加密的文件 如c:/加密后文件.txt * @param destFile 解密后存放的文件名 如c:/
	 *            test/解密后文件.txt
	 */
	public void decrypt(String file, String dest) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
//		Cipher cipher = Cipher.getInstance("DES");
//		cipher.init(Cipher.DECRYPT_MODE, this.key);
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(dest);
		CipherOutputStream cos = new CipherOutputStream(out, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = is.read(buffer)) >= 0) {
			System.out.println();
			cos.write(buffer, 0, r);
		}
		cos.close();
		out.close();
		is.close();
	}

	@Test
	public void test() {
		try {
			String test = "Hello world, 我是加解密";
			FileEncryptor td = new FileEncryptor("aaa");
			byte[] data = td.encrypt(test.getBytes()); // 加密
			byte[] data2 = td.decrypt(data); // 解密
			System.out.println("Testing...");
			System.out.println(new String(data2));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
