package com.wisdge.utils.security;

import org.springframework.util.Base64Utils;

/**
 * Description: Kaiser Encryptor Factory Copyright(c) 2013 Wisdge.com
 * 
 * @author Kevin MOU
 * @version 1.1
 */
public class KaiserEncryptor {
	public static final int INT_PRIM_NUMBER = 95;
	public static final int INT_RETURN_LOOP = 94;

	/**
	 * 解密方法
	 * 
	 * @param strCode
	 *            需解密的字符串
	 * @return 解密后的字符串
	 */
	public static String decode(String strCode) {
		if (strCode == null || strCode.length() == 0) {
			return "";
		}

		String preDecode = doDecode(strCode);
		// System.out.println(preDecode);
		try {
			byte[] bt = Base64Utils.decodeFromString(preDecode);
			return new String(bt);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String doDecode(String strCode) {
		String strOriginal;
		String strRnd;
		int intRnd;
		int intStrLen;
		intStrLen = strCode.length() - 1;
		strRnd = strCode.substring(intStrLen / 2, intStrLen / 2 + 1);
		intRnd = strRnd.hashCode() - new KaiserEncryptor().startChar();
		strCode = strCode.substring(0, intStrLen / 2) + strCode.substring(intStrLen / 2 + 1, intStrLen + 1);
		strOriginal = new KaiserEncryptor().loopCode(strCode, INT_RETURN_LOOP - intRnd);

		return strOriginal;
	}

	/**
	 * 加密方法.随机取得加密的循环次数，使得每次加密所得的秘文会有所不同
	 * 
	 * @param strOriginal
	 *            需加密的字符串
	 * @return 加密后的字符串
	 */
	public static String encode(String strOriginal) {
		// prepaire encrypt
		if (strOriginal == null || strOriginal.length() == 0) {
			return "";
		}
		String preEncrypt = Base64Utils.encodeToString(strOriginal.getBytes());
		// System.out.println(preEncrypt);

		return doEncode(preEncrypt);
	}

	private static String doEncode(String strOriginal) {
		int intRnd;
		char rnd;
		int intStrLen;
		String strCode;
		String strCodeMe = "";
		// 2 到 93 之间的随即数，即同一原文可能获得93种不同的秘文
		intRnd = (int) (Math.random() * (INT_RETURN_LOOP - 2) + 2);
		strCode = new KaiserEncryptor().loopCode(strOriginal, intRnd);
		// 对随机数作偏移加密
		rnd = (char) (intRnd + new KaiserEncryptor().startChar());
		intStrLen = strCode.length();
		strCodeMe = strCode.substring(0, intStrLen / 2) + rnd + strCode.substring(intStrLen / 2, intStrLen);
		if (strCodeMe.indexOf("'") >= 0) {
			return doEncode(strOriginal);
		} else {
			return strCodeMe;
		}
	}

	/**
	 * 基础的凯撒算法,并对于每一位增加了偏移
	 * 
	 * @param strOriginal
	 *            需加密的字符串
	 * @return 加密后的字符串
	 */
	private String kaiserCode(String strOriginal) {
		int intChar;
		String strCode;
		int i;
		int intStrLen;
		int intTmp;
		intStrLen = strOriginal.length();
		strCode = "";
		for (i = 0; i < intStrLen; i++) {
			intChar = strOriginal.substring(i, i + 1).hashCode();
			intTmp = intChar - this.startChar();
			intTmp = (intTmp * INT_PRIM_NUMBER + i + 1) % this.maxChar() + this.startChar();
			strCode = strCode + (char) (intTmp);
		}
		return strCode;
	}

	// 循环调用凯撒算法一定次数后，可以取得原文
	private String loopCode(String strOriginal, int intLoopCount) {
		String strCode;
		int i;
		strCode = strOriginal;
		for (i = 0; i < intLoopCount; i++) {
			strCode = this.kaiserCode(strCode);
		}
		return strCode;
	}

	private int maxChar() {
		String str1 = "~";
		String str2 = "!";
		return str1.hashCode() - str2.hashCode() + 1;
	}

	private int startChar() {
		String str1 = "!";
		return str1.hashCode();
	}

}
