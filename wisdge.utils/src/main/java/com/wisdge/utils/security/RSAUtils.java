package com.wisdge.utils.security;

import org.apache.commons.net.util.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

//	/**
//	 * RSA最大加密明文大小
//	 */
//	private static final int MAX_ENCRYPT_BLOCK = 117;
//
//	/**
//	 * RSA最大解密密文大小
//	 */
//	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * <p>
	 * 生成密钥对(公钥和私钥)
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genKeyPair(int keySize) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(keySize);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 默认key长度为1024 bit
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genKeyPair() throws Exception {
		return genKeyPair(1024);
	}

	/**
	 * <p>
	 * 用私钥对信息生成数字签名
	 * </p>
	 * 
	 * @param data
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return Base64.encodeBase64String(signature.sign());
	}
	public static String sign(String encryptedString, String privateKey) throws Exception {
		return sign(Base64.decodeBase64(encryptedString), privateKey);
	}

	/**
	 * <p>
	 * 校验数字签名
	 * </p>
	 * 
	 * @param data
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @param sign
	 *            数字签名
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}
	public static boolean verify(String encryptedString, String publicKey, String sign) throws Exception {
		return verify(Base64.decodeBase64(encryptedString), publicKey, sign);
	}
	
	/**
	 * <P>
	 * 私钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		int maxBlock = getMaxBlock(privateKey, Cipher.DECRYPT_MODE, true);
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > maxBlock) {
				cache = cipher.doFinal(encryptedData, offSet, maxBlock);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * maxBlock;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}
	
	public static String decryptByPrivateKey(String encryptedData, String privateKey) throws Exception {
		byte[] decryptBytes = decryptByPrivateKey(Base64.decodeBase64(encryptedData), privateKey);
		return new String(decryptBytes);
	}

	/**
	 * <p>
	 * 公钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		int maxBlock = getMaxBlock(publicKey, Cipher.DECRYPT_MODE, false);
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > maxBlock) {
				cache = cipher.doFinal(encryptedData, offSet, maxBlock);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * maxBlock;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}
	
	public static String decryptByPublicKey(String encryptedData, String publicKey) throws Exception {
		byte[] decryptBytes = decryptByPublicKey(Base64.decodeBase64(encryptedData), publicKey);
		return new String(decryptBytes);
	}

	/**
	 * <p>
	 * 公钥加密
	 * </p>
	 * 
	 * @param data
	 *            源数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		int maxBlock = getMaxBlock(publicKey, Cipher.ENCRYPT_MODE, false);
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > maxBlock) {
				cache = cipher.doFinal(data, offSet, maxBlock);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * maxBlock;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	public static String encryptByPublicKey(String content, String publicKey) throws Exception {
		byte[] encryptBytes = encryptByPublicKey(content.getBytes(StandardCharsets.UTF_8), publicKey);
		return Base64.encodeBase64String(encryptBytes);
	}
	
	/**
	 * <p>
	 * 私钥加密
	 * </p>
	 * 
	 * @param data
	 *            源数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		int maxBlock = getMaxBlock(privateKey, Cipher.ENCRYPT_MODE, true);
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > maxBlock) {
				cache = cipher.doFinal(data, offSet, maxBlock);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * maxBlock;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}
	public static String encryptByPrivateKey(String content, String privateKey) throws Exception {
		byte[] encryptBytes = encryptByPrivateKey(content.getBytes(StandardCharsets.UTF_8), privateKey);
		return Base64.encodeBase64String(encryptBytes);
	}

	/**
	 * 获取私钥
	 * @param keyMap 密钥对
	 * @return 私钥base64字符串
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 获取公钥
	 * @param keyMap 密钥对
	 * @return 公钥base64字符串
	 */
	public static String getPublicKey(Map<String, Object> keyMap) {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	private static RSAPublicKey getRSAPublidKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return (RSAPublicKey)keyFactory.generatePublic(keySpec);
	}

	private static RSAPrivateKey getRSAPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyStr));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 根据密钥计算加解密时候分段最大长度
	 * @param key 密码base64字符串
	 * @param mode 加密还是解密
	 * @param isPrivate 私钥还是公钥
	 * @return 最大分段长度
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private static int getMaxBlock(String key, int mode, boolean isPrivate) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		int keySize;
		if (isPrivate) {
			RSAPrivateKey rsaPrivateKey = getRSAPrivateKey(key);
			RSAPrivateKeySpec keySpec = keyFactory.getKeySpec(rsaPrivateKey, RSAPrivateKeySpec.class);
			keySize = keySpec.getModulus().bitLength();
		} else {
			RSAPublicKey rsaPublidKey = getRSAPublidKey(key);
			RSAPublicKeySpec keySpec = keyFactory.getKeySpec(rsaPublidKey, RSAPublicKeySpec.class);
			keySize = keySpec.getModulus().bitLength();
		}
		int maxBlock;
		if (mode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		return maxBlock;
	}

	@Test
	public void test() throws Exception {
		String publicKey = null;
		String privateKey = null;
		try {
			Map<String, Object> keyMap = RSAUtils.genKeyPair();
			publicKey = RSAUtils.getPublicKey(keyMap);
			privateKey = RSAUtils.getPrivateKey(keyMap);
			System.err.println("公钥: \n\r" + publicKey);
			System.err.println("私钥： \n\r" + privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

        System.out.println("公钥加密——私钥解密");
		String content = "这是一行测试RSA数字签名的无意义文字";
		System.out.println("\r加密前文字：\r\n" + content);
		String encrypted = RSAUtils.encryptByPublicKey(content, publicKey);
		System.out.println("加密后文字：\r\n" + encrypted);
		System.out.println("解密后文字: \r\n" + RSAUtils.decryptByPrivateKey(encrypted, privateKey));
	}
	
	@Test
	public void test2() throws Exception {
		String publicKey = null;
		String privateKey = null;
		try {
			Map<String, Object> keyMap = RSAUtils.genKeyPair(2048);
			publicKey = RSAUtils.getPublicKey(keyMap);
			privateKey = RSAUtils.getPrivateKey(keyMap);
			System.err.println("公钥: \n\r" + publicKey);
			System.err.println("私钥： \n\r" + privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

        System.out.println("\r\n私钥加密——公钥解密");
        String content = "这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字," +
				"这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,这是一行测试RSA数字签名的无意义文字,";
        System.out.println("\r加密前文字：\r\n" + content);
        String encrypted = RSAUtils.encryptByPrivateKey(content, privateKey);
        System.out.println("加密后文字：\r\n" + encrypted);
        String decrypted = RSAUtils.decryptByPublicKey(encrypted, publicKey);
        System.out.println("解密后文字: \r\n" + decrypted);
        
        System.out.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encrypted, privateKey);
        System.out.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encrypted, publicKey, sign);
        System.out.println("验证结果:\r" + status);
    }

	@Test
	public static void keys() {
		String publicKey = null;
		String privateKey = null;
		try {
			Map<String, Object> keyMap = RSAUtils.genKeyPair();
			publicKey = RSAUtils.getPublicKey(keyMap);
			privateKey = RSAUtils.getPrivateKey(keyMap);
			System.out.println("公钥: \n\r" + publicKey);
			System.out.println("私钥： \n\r" + privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
