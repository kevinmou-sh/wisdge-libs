package com.wisdge.utils;

public class PasswordUtils {

	private PasswordUtils() {
		throw new IllegalStateException("PasswordUtils class");
	}

	/**
	 * 密码是否是正序或反序连续4位及以上
	 * 
	 * @param pwd
	 * @return true为正确，false为错误。
	 */
	public static boolean isContinuous(String pwd, boolean includeFlow) throws PasswordInvalidException {
		int count = 0; // 正序次数
		int flowCount = 0; // 键盘顺序
		int reverseCount = 0; // 反序次数
		String[] strArr = pwd.split("");
		for (int i = 0; i < strArr.length - 1; i++) {
			// 从1开始是因为划分数组时，第一个为空
			if (isPositiveContinuous(strArr[i], strArr[i + 1])) {
				count++;
			} else {
				count = 0;
			}
			if (isPositiveFlowkey(strArr[i].charAt(0), strArr[i + 1].charAt(0))) {
				flowCount++;
			} else {
				flowCount = 0;
			}
			if (isReverseContinuous(strArr[i], strArr[i + 1])) {
				reverseCount++;
			} else {
				reverseCount = 0;
			}
			if (count > 2 || reverseCount > 2 || (includeFlow && flowCount > 2))
				break;
		}
		if (count > 2 || reverseCount > 2)
			throw new PasswordInvalidException(-5, "正序或反序连续4位及以上");
		if (includeFlow && flowCount > 2)
			throw new PasswordInvalidException(-6, "键盘连续相邻的字母4位及以上");

		return true;
	}

	/**
	 * 是否是正序连续
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isPositiveContinuous(String str1, String str2) {
		if (str2.hashCode() - str1.hashCode() == 1)
			return true;
		return false;
	}

	static String[] lines = new String[] { "qwertyuiop[]\\", "QWERTYUIOP{}|", "asdfghjkl;'", "ASDFGHJKL:\"", "zxcvbnm,./", "ZXCVBNM<>?" };

	public static boolean isPositiveFlowkey(char c1, char c2) {
		for (String line : lines) {
			int i = line.indexOf(c1);
			if (i != -1) {
				if (line.length() > (i + 1) && line.charAt(i + 1) == c2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否是反序连续
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isReverseContinuous(String str1, String str2) {
		if (str2.hashCode() - str1.hashCode() == -1)
			return true;
		else
			return false;
	}

	/**
	 * - 字母区分大小写，可输入符号<br/>
	 * - 密码必须同时包含字母和数字<br/>
	 * - 密码长度8-20位<br/>
	 * - 密码中不能存在连续4个及以上的数字或字母（如：1234、7654、abcd、defgh等）<br/>
	 * 
	 * @param password 密码
	 * @param min      最短长度
	 * @param level    密码安全等级
	 * @return true为正确，false为错误
	 */
	public static boolean isAvailable(String password, int min, int level) throws PasswordInvalidException {
		if (password.length() < min)
			throw new PasswordInvalidException(-1, "不符合长度：" + min);
		if (level < 1)
			return true;

		// level > 0
		if (!password.matches(".*?[a-z]+.*?") || !password.matches(".*?[A-Z]+.*?")) {
			throw new PasswordInvalidException(-2, "缺少大小写字母");
		}
		if (level > 1 && !password.matches(".*?[\\d]+.*?")) {
			throw new PasswordInvalidException(-3, "缺少数字");
		}
		if (level > 2 && !password.matches(".*?[^a-zA-Z\\d]+.*?")) {
			throw new PasswordInvalidException(-4, "缺少特殊字符");
		}
		if (level > 3)
			return isContinuous(password, true);

		return true;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(PasswordUtils.isAvailable("Letmein_0308", 8, 4));
		} catch (PasswordInvalidException e) {
			int errorCode = e.getCode();
			System.err.println(errorCode);
			/*
			 * -1: 不符合长度
			 * -2: 缺少大小写字母
			 * -3: 缺少数字
			 * -4: 缺少特殊字符
			 * -5: 正序或反序连续4位及以上
			 * -6: 键盘连续相邻的字母4位及以上
			 */
		}
	}
}
