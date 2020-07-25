package com.wisdge.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PasswordUtils {
	public static int RULE_NONE = 0;
	public static int RULE_ALLCASE = 1 << 0;
	public static int RULE_DIGIT = 1 << 1;
	public static int RULE_SPECIAL = 1 << 2;
	public static int RULE_CONTINUOUS = 1 << 3;

	public static int ERROR_LESS = -1;
	public static int ERROR_SENSITIVE = -2;
	public static int ERROR_DIGIT_MISSING = -3;
	public static int ERROR_SPECIAL_MISSING = -4;
	public static int ERROR_CONTINUOUS_NATURE = -5;
	public static int ERROR_CONTINUOUS_KEYBOARD = -6;

	public PasswordUtils () {
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
			throw new PasswordInvalidException(ERROR_CONTINUOUS_NATURE, "正序或反序连续4位及以上");
		if (includeFlow && flowCount > 2)
			throw new PasswordInvalidException(ERROR_CONTINUOUS_KEYBOARD, "键盘连续相邻的字母4位及以上");

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
	 * 		-1: 不符合长度
	 * 		-2: 缺少大小写字母
	 * 		-3: 缺少数字
	 * 		-4: 缺少特殊字符
	 * 		-5: 正序或反序连续4位及以上
	 * 		-6: 键盘连续相邻的字母4位及以上
	 * @param password 密码
	 * @param min      最短长度
	 * @param level    密码安全等级
	 * @return true为正确，false为错误
	 */
	public static boolean isAvailable(String password, int min, int level) throws PasswordInvalidException {
		if (password.length() < min)
			throw new PasswordInvalidException(ERROR_LESS, "不符合长度：" + min);
		if (level < 1)
			return true;

		// level > 0
		if (!password.matches(".*?[a-z]+.*?") || !password.matches(".*?[A-Z]+.*?")) {
			throw new PasswordInvalidException(ERROR_SENSITIVE, "缺少大小写字母");
		}
		if (level > 1 && !password.matches(".*?[\\d]+.*?")) {
			throw new PasswordInvalidException(ERROR_DIGIT_MISSING, "缺少数字");
		}
		if (level > 2 && !password.matches(".*?[^a-zA-Z\\d]+.*?")) {
			throw new PasswordInvalidException(ERROR_SPECIAL_MISSING, "缺少特殊字符");
		}
		if (level > 3)
			return isContinuous(password, true);

		return true;
	}

	/**
	 * - 字母区分大小写，可输入符号<br/>
	 * - 密码必须同时包含字母和数字<br/>
	 * - 密码长度8-20位<br/>
	 * - 密码中不能存在连续4个及以上的数字或字母（如：1234、7654、abcd、defgh等）<br/>
	 *
	 * @param password String 密码
	 * @param min	int 最短长度
	 * @param role	int 密码安全规则
	 * @return boolean true为正确，false为错误
	 **/
	public static boolean match(String password, int min, int role) throws PasswordInvalidException {
		if (password.length() < min)
			throw new PasswordInvalidException(ERROR_LESS, "不符合长度：" + min);

		if (role == PasswordUtils.RULE_NONE)
			return true;

		if ((role & PasswordUtils.RULE_ALLCASE) == PasswordUtils.RULE_ALLCASE) {
			if (!password.matches(".*?[a-z]+.*?") || !password.matches(".*?[A-Z]+.*?"))
				throw new PasswordInvalidException(ERROR_SENSITIVE, "缺少大小写字母");
		}
		if ((role & PasswordUtils.RULE_DIGIT) == PasswordUtils.RULE_DIGIT) {
			if (!password.matches(".*?[\\d]+.*?"))
				throw new PasswordInvalidException(ERROR_DIGIT_MISSING, "缺少数字");
		}
		if ((role & PasswordUtils.RULE_SPECIAL) == PasswordUtils.RULE_SPECIAL) {
			if (!password.matches(".*?[^a-zA-Z\\d]+.*?"))
				throw new PasswordInvalidException(ERROR_SPECIAL_MISSING, "缺少特殊字符");
		}
		if ((role & PasswordUtils.RULE_CONTINUOUS) == PasswordUtils.RULE_CONTINUOUS) {
			return isContinuous(password, true);
		}
		return true;
	}

	public static final char[] allowedSpecialCharactors = {
			'`', '~', '@', '#', '$', '%', '^', '&',
			'*', '(', ')', '-', '_', '=', '+', '[',
			'{', '}', ']', '\\', '|', ';', ':', '"',
			'\'', ',', '<', '.', '>', '/', '?'};//密码能包含的特殊字符
	private static final int letterRange = 26;
	private static final int numberRange = 10;
	private static final int spCharactorRange = allowedSpecialCharactors.length;
	private static final Random random = new Random();

	public static String random() {
		return random(8);
	}
	public static String random(int length) {
		if (length < 8)
			length = 0;

		char[] password = new char[length];
		List<Integer> pwCharsIndex = new ArrayList();
		for (int i = 0; i < password.length; i++) {
			pwCharsIndex.add(i);
		}
		List<CharactorType> takeTypes = new ArrayList(Arrays.asList(CharactorType.values()));
		List<CharactorType> fixedTypes = Arrays.asList(CharactorType.values());
		int typeCount = 0;
		while (pwCharsIndex.size() > 0) {
			int pwIndex = pwCharsIndex.remove(random.nextInt(pwCharsIndex.size()));//随机填充一位密码
			Character c;
			if (typeCount < CharactorType.values().length) {
				//生成不同种类字符
				c = generateCharacter(takeTypes.remove(random.nextInt(takeTypes.size())));
				typeCount ++;
			} else {//随机生成所有种类密码
				c = generateCharacter(fixedTypes.get(random.nextInt(fixedTypes.size())));
			}
			password[pwIndex] = c.charValue();
		}
		return String.valueOf(password);
	}

	private static Character generateCharacter(CharactorType type) {
		Character c = null;
		int rand;
		switch (type) {
			case LOWERCASE://随机小写字母
				rand = random.nextInt(letterRange);
				rand += 97;
				c = new Character((char) rand);
				break;
			case UPPERCASE://随机大写字母
				rand = random.nextInt(letterRange);
				rand += 65;
				c = new Character((char) rand);
				break;
			case NUMBER://随机数字
				rand = random.nextInt(numberRange);
				rand += 48;
				c = new Character((char) rand);
				break;
			case SPECIAL_CHARACTOR://随机特殊字符
				rand = random.nextInt(spCharactorRange);
				c = new Character(allowedSpecialCharactors[rand]);
				break;
		}
		return c;
	}

	@Test
	public void test() throws PasswordInvalidException {
		int role = PasswordUtils.RULE_ALLCASE | PasswordUtils.RULE_DIGIT | PasswordUtils.RULE_SPECIAL | PasswordUtils.RULE_CONTINUOUS;
		System.out.println(role);
		System.out.println(PasswordUtils.RULE_ALLCASE);
		System.out.println(PasswordUtils.RULE_DIGIT);
		System.out.println(PasswordUtils.RULE_SPECIAL);
		System.out.println(PasswordUtils.RULE_CONTINUOUS);
		System.out.println((role & PasswordUtils.RULE_ALLCASE) == PasswordUtils.RULE_ALLCASE);
		System.out.println((role & PasswordUtils.RULE_DIGIT) == PasswordUtils.RULE_DIGIT);
		System.out.println((role & PasswordUtils.RULE_SPECIAL) == PasswordUtils.RULE_SPECIAL);
		System.out.println((role & PasswordUtils.RULE_CONTINUOUS) == PasswordUtils.RULE_CONTINUOUS);

		System.out.println(PasswordUtils.match("Letmein_0308", 8, role));
		System.out.println(PasswordUtils.random());
	}
}

enum CharactorType {
	LOWERCASE,
	UPPERCASE,
	NUMBER,
	SPECIAL_CHARACTOR
}