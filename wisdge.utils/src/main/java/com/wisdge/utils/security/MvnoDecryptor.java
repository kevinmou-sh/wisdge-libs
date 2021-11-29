package com.wisdge.utils.security;

import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class MvnoDecryptor {
	private static final char[] DIGITS = "1234567890".toCharArray();
    private static final char[] CHARS = "-1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String key;

    public MvnoDecryptor(String key) {
        this.key = key;
    }

    protected static String decrypt(String message, String key) throws Exception {
        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    protected static byte[] encrypt(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    protected static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        return digest;
    }

    /* (non-Javadoc)
     * @see com.elite.ngs.utils.Decryptor#encrypt(java.lang.String)
     */
    public String encrypt(String original) {
        StringBuilder builder = new StringBuilder();
        builder.append(random(18, CHARS));
        builder.append(random(8, DIGITS));
        builder.append(System.currentTimeMillis() - 1230736351231L);
        builder.append(random(5, DIGITS));
        builder.append(random(15, CHARS));
        builder.append(original);
        builder.append(random(10, CHARS));
        original = builder.toString();
        try {
            String text = java.net.URLEncoder.encode(original, "UTF-8");
            return toHexString(encrypt(text, key));
        }
        catch (Exception e) {
            throw new RuntimeException("加密时出错", e);
        }
    }

    /**
     * 解密
     * @param cryptograph 密文
     * @return 用户名
     * @throws Exception
     */
    public String decrypt(String cryptograph) throws Exception {
        try {
            String original = java.net.URLDecoder.decode(decrypt(cryptograph, key), "UTF-8");
            String t = original.substring(26, 38);
            long time = 1230736351231L + Long.parseLong(t);
            if (Math.abs(System.currentTimeMillis() - time) > 43200000) { // 如果时间差距超过12小时
                throw new IllegalArgumentException("密文时间戳错误");
            }
            return original.substring(58, original.length() - 10);
        }
        catch (Exception e) {
            throw new RuntimeException("解密时出错", e);
        }
    }

    protected static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (byte element : b) {
            String plainText = Integer.toHexString(0xff & element);
            if (plainText.length() < 2) {
                plainText = "0" + plainText;
            }
            hexString.append(plainText);
        }

        return hexString.toString();
    }

    protected static String random(int count, char[] chars) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            builder.append(chars[random.nextInt(chars.length)]);
        }
        return builder.toString();
    }
}
