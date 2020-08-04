package com.wisdge.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class PasswordUtils {
	public static int RULE_NONE = 0;					// 无规则
	public static int RULE_ALLCASE = 1 << 0;			// 必须包含大小写字符
	public static int RULE_DIGIT = 1 << 1;				// 必须包含数字
	public static int RULE_SPECIAL = 1 << 2;			// 必须包含特殊字符
	public static int RULE_CONTINUOUS_NATURE = 1 << 3;	// 必须少于3个以上连续相邻字符
	public static int RULE_CONTINUOUS_KEYBOARD = 1 << 4;// 必须少于3个以上连续键盘相邻字符

	public static int ERROR_EMPTY = 0;	// 密码为空
	public static int ERROR_LESS = -1; // 小于最小长度要求
	public static int ERROR_OVERFLOW = -2; // 大于最大长度要求
	public static int ERROR_CASE_SENSITIVE = -3;	// 缺少大小写字母
	public static int ERROR_DIGIT_MISSING = -4;	// 缺少数字
	public static int ERROR_SPECIAL_MISSING = -5;	// 缺少特殊字符
	public static int ERROR_CONTINUOUS_NATURE = -6;	// 连续相邻3个以上字符
	public static int ERROR_CONTINUOUS_KEYBOARD = -7;	// 连续相邻3个以上键盘字符
	public static int ERROR_WORD_SENSITIVE = -8;	// 出现关键敏感词

	public PasswordUtils () {
	}

	/**
	 * 密码是否是正序或反序连续4位及以上相邻字符
	 * 
	 * @param password String
	 * @return boolean
	 */
	private static boolean isContinuousNature(String password) throws PasswordInvalidException {
		int count = 0; // 正序次数
		int reverseCount = 0; // 反序次数
		String[] strArr = password.split("");
		for (int i = 0; i < strArr.length - 1; i++) {
			// 从1开始是因为划分数组时，第一个为空
			if (isPositiveContinuous(strArr[i], strArr[i + 1])) {
				count++;
			} else {
				count = 0;
			}
			if (isReverseContinuous(strArr[i], strArr[i + 1])) {
				reverseCount++;
			} else {
				reverseCount = 0;
			}
			if (count > 2 || reverseCount > 2)
				return false;
		}
		return true;
	}

	/**
	 * 密码是否是正序或反序连续4位及以上相邻字符
	 *
	 * @param password String
	 * @return boolean
	 */
	private static boolean isContinuousKeyboard(String password) throws PasswordInvalidException {
		int flowCount = 0; // 键盘顺序
		String[] strArr = password.split("");
		for (int i = 0; i < strArr.length - 1; i++) {
			if (isPositiveFlowkey(strArr[i].charAt(0), strArr[i + 1].charAt(0))) {
				flowCount ++;
			} else {
				flowCount = 0;
			}
			if (flowCount > 2)
				return false;
		}
		return true;
	}

	/**
	 * 是否是正序连续
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	private static boolean isPositiveContinuous(String str1, String str2) {
		if (str2.hashCode() - str1.hashCode() == 1)
			return true;
		return false;
	}

	private static String[] lines = new String[] { "qwertyuiop[]\\", "QWERTYUIOP{}|", "asdfghjkl;'", "ASDFGHJKL:\"", "zxcvbnm,./", "ZXCVBNM<>?" };
	private static boolean isPositiveFlowkey(char c1, char c2) {
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
	private static boolean isReverseContinuous(String str1, String str2) {
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
			throw new PasswordInvalidException(ERROR_CASE_SENSITIVE, "缺少大小写字母");
		}
		if (level > 1 && !password.matches(".*?[\\d]+.*?")) {
			throw new PasswordInvalidException(ERROR_DIGIT_MISSING, "缺少数字");
		}
		if (level > 2 && !password.matches(".*?[^a-zA-Z\\d]+.*?")) {
			throw new PasswordInvalidException(ERROR_SPECIAL_MISSING, "缺少特殊字符");
		}
		if (level > 3) {
			if (! isContinuousNature(password))
				throw new PasswordInvalidException(ERROR_CONTINUOUS_NATURE, "相邻字符超过3个");
			else if (! isContinuousKeyboard(password))
				throw new PasswordInvalidException(ERROR_CONTINUOUS_KEYBOARD, "相邻键盘字符超过3个");
		}
		return true;
	}

	/**
	 * @param password String 密码
	 * @param min	int 最小长度
	 * @param max	int 最大长度
	 * @param role	int 密码安全规则
	 * @return int 返回1为验证通过，否则为不通过
	 **/
	public static int match(String password, int min, int max, int role) throws PasswordInvalidException {
		return match(password, min, max, role, null);
	}

	/**
	 * @param password String 密码
	 * @param min	int 最小长度
	 * @param max	int 最大长度
	 * @param role	int 密码安全规则
	 * @param excludes List 需要过滤的敏感词
	 * @return int 返回1为验证通过，否则为不通过
	 **/
	public static int match(String password, int min, int max, int role, List<String> excludes) throws PasswordInvalidException {
		if (StringUtils.isEmpty(password))
			return ERROR_EMPTY;
		if (password.length() < min)
			return ERROR_LESS;
		if (password.length() > max)
			return ERROR_OVERFLOW;

		if (role == PasswordUtils.RULE_NONE)
			return 1;

		if ((role & PasswordUtils.RULE_ALLCASE) == PasswordUtils.RULE_ALLCASE) {
			if (!password.matches(".*?[a-z]+.*?") || !password.matches(".*?[A-Z]+.*?"))
				return ERROR_CASE_SENSITIVE;
		}
		if ((role & PasswordUtils.RULE_DIGIT) == PasswordUtils.RULE_DIGIT) {
			if (!password.matches(".*?[\\d]+.*?"))
				return ERROR_DIGIT_MISSING;
		}
		if ((role & PasswordUtils.RULE_SPECIAL) == PasswordUtils.RULE_SPECIAL) {
			if (!password.matches(".*?[^a-zA-Z\\d]+.*?"))
				return ERROR_SPECIAL_MISSING;
		}
		if ((role & PasswordUtils.RULE_CONTINUOUS_NATURE) == PasswordUtils.RULE_CONTINUOUS_NATURE) {
			if (! isContinuousNature(password))
				return ERROR_CONTINUOUS_NATURE;
		}
		if ((role & PasswordUtils.RULE_CONTINUOUS_KEYBOARD) == PasswordUtils.RULE_CONTINUOUS_KEYBOARD) {
			if (! isContinuousKeyboard(password))
				return ERROR_CONTINUOUS_KEYBOARD;
		}

		if (excludes != null) {
			for(String exclude: excludes) {
				//log.info("{} vs {}", password, exclude);
				if (password.indexOf(exclude) != -1)
					return ERROR_WORD_SENSITIVE;
			}
		}

		return 1;
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
		int role = PasswordUtils.RULE_ALLCASE | PasswordUtils.RULE_DIGIT |
				PasswordUtils.RULE_SPECIAL | PasswordUtils.RULE_CONTINUOUS_NATURE | PasswordUtils.RULE_CONTINUOUS_KEYBOARD;
		// System.out.println(role);
		System.out.println("AllCase: " + ((role & PasswordUtils.RULE_ALLCASE) == PasswordUtils.RULE_ALLCASE));
		System.out.println("Digit: " + ((role & PasswordUtils.RULE_DIGIT) == PasswordUtils.RULE_DIGIT));
		System.out.println("Special: " + ((role & PasswordUtils.RULE_SPECIAL) == PasswordUtils.RULE_SPECIAL));
		System.out.println("ContinuousNature: " + ((role & PasswordUtils.RULE_CONTINUOUS_NATURE) == PasswordUtils.RULE_CONTINUOUS_NATURE));
		System.out.println("ContinuousKeyboard: " + ((role & PasswordUtils.RULE_CONTINUOUS_KEYBOARD) == PasswordUtils.RULE_CONTINUOUS_KEYBOARD));

		List<String> excludes = new ArrayList<>();
		excludes.add("mein");
		int code = PasswordUtils.match("Letmein_0308", 8, 20, PasswordUtils.RULE_ALLCASE |
				PasswordUtils.RULE_DIGIT |
				PasswordUtils.RULE_SPECIAL |
				PasswordUtils.RULE_CONTINUOUS_NATURE |
				PasswordUtils.RULE_CONTINUOUS_KEYBOARD, excludes);
		System.out.println(code);

	}
}

enum CharactorType {
	LOWERCASE,
	UPPERCASE,
	NUMBER,
	SPECIAL_CHARACTOR
}