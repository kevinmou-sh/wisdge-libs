package com.wisdge.utils;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Random utility class
 * @author Kevin MOU
 */
@Slf4j
public class RandomUtils extends org.apache.commons.lang3.RandomUtils {
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final String ALPHABETIC_CHOOSE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String STRING_CHOOSE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * 获得一个指定长度的随机数字串
	 *
	 * @param codeLength
	 *            数字串的长度
	 * @return String对象， 随机数字串
	 */
	public static String getNumber(int codeLength) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < codeLength; i++) {
			buf.append(secureRandom.nextInt(10));
		}
		return buf.toString();
	}

	/**
	 * 获得一个指定长度的随机字母的字符串
	 *
	 * @param codeLength
	 *            随机字符串的长度
	 * @return String 随机字符串
	 */
	public static String getAlphabetic(int codeLength) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < codeLength; i++) {
			buf.append(ALPHABETIC_CHOOSE.charAt(secureRandom.nextInt(ALPHABETIC_CHOOSE.length())));
		}
		return buf.toString();
	}

	/**
	 * 获得一个指定长度的随机数字和字母混合的字符串
	 *
	 * @param codeLength
	 *            随机字符串的长度
	 * @return String 随机字符串
	 */
	public static String getString(int codeLength) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < codeLength; i++) {
			buf.append(STRING_CHOOSE.charAt(secureRandom.nextInt(STRING_CHOOSE.length())));
		}
		return buf.toString();
	}

	public static String getGUID() {
		UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString().toUpperCase();
	}

	public static String getRandomId() {
		return org.apache.commons.codec.digest.DigestUtils.md5Hex(getGUID());
	}

	/**
	 * 获得一个随机生成的带时间戳的字符串，由指定长度的随机字符串和时间戳组合而成。中间以 '_' 符号连接
	 *
	 * @param codeLength
	 *            随机字符串的随机串长度
	 * @return String 带时间戳的随机字符串
	 */
	public static String getStringWithTimestamp(int codeLength) {
		Date nowtime = new Date();
		String filename = getString(codeLength) + "_" + String.valueOf(nowtime.getTime());
		return filename;
	}
}

@Slf4j
class RandomGUID {
	public String valueBeforeMD5 = "";
	public String valueAfterMD5 = "";
	private static SecureRandom mySecureRand;

	/*
	 * Default constructor. With no specification of security option, this constructor defaults to lower security, high performance.
	 */
	public RandomGUID() {
		getRandomGUID();
	}

	/*
	 * Method to generate the random GUID
	 */
	private void getRandomGUID() {
		MessageDigest md5 = null;
		StringBuffer bufBeforeMD5 = new StringBuffer();

		try {
			md5 = MessageDigest.getInstance("MD5");
			InetAddress id = InetAddress.getLocalHost();
			long time = System.currentTimeMillis();
			long rand = mySecureRand.nextLong();

			// This StringBuffer can be a long as you need; the MD5
			// hash will always return 128 bits. You can change
			// the seed to include anything you want here.
			// You could even stream a file through the MD5 making
			// the odds of guessing it at least as great as that
			// of guessing the contents of the file!
			bufBeforeMD5.append(id.toString());
			bufBeforeMD5.append(":");
			bufBeforeMD5.append(Long.toString(time));
			bufBeforeMD5.append(":");
			bufBeforeMD5.append(Long.toString(rand));

			valueBeforeMD5 = bufBeforeMD5.toString();
			md5.update(valueBeforeMD5.getBytes());
			byte[] array = md5.digest();
			StringBuffer sb = new StringBuffer();
			for (byte element : array) {
				int b = element & 0xFF;
				if (b < 0x10) {
					sb.append('0');
				}
				sb.append(Integer.toHexString(b));
			}
			valueAfterMD5 = sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		String raw = valueAfterMD5.toUpperCase();
		if (raw.length() < 20)
			throw new NullPointerException("获取GUID异常：" + raw);

		StringBuffer sb = new StringBuffer();
		sb.append(raw.substring(0, 8));
		sb.append("-");
		sb.append(raw.substring(8, 12));
		sb.append("-");
		sb.append(raw.substring(12, 16));
		sb.append("-");
		sb.append(raw.substring(16, 20));
		sb.append("-");
		sb.append(raw.substring(20));

		return sb.toString();
	}

}
