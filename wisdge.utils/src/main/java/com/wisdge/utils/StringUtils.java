package com.wisdge.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin MOU
 * @version 1.1
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	/**
	 * The empty String {@code ""}.
	 *
	 * @since 1.0
	 */
	public static final String EMPTY = "";

	/**
	 * Represents a failed index search.
	 *
	 * @since 1.0
	 */
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * <p>
	 * The maximum size to which the padding constant(s) can expand.
	 * </p>
	 */
	private static final int PAD_LIMIT = 8192;

	// Default abbreviate suffix string
	private static final String ABBREVIATE_SUFFIX = "...";

	/**
	 * The default line-break string used by the methods in this class.
	 */
	public static final String LINE_BREAK = System.getProperty("line.separator");

	/**
	 * HTML encoding (does not convert line breaks). Replaces all '&gt;' '&lt;' '&amp;' and '"' with entity reference
	 *
	 * @param htmlStr
	 *            html字符串
	 * @return 编译后的字符串
	 */
	public static String htmlEnc(String htmlStr) {
		int ln = htmlStr.length();
		for (int i = 0; i < ln; i++) {
			char c = htmlStr.charAt(i);
			if (c == '<' || c == '>' || c == '&' || c == '"') {
				StringBuffer b = new StringBuffer(htmlStr.substring(0, i));
				switch (c) {
				case '<':
					b.append("&lt;");
					break;
				case '>':
					b.append("&gt;");
					break;
				case '&':
					b.append("&amp;");
					break;
				case '"':
					b.append("&quot;");
					break;
				}
				i++;
				int next = i;
				while (i < ln) {
					c = htmlStr.charAt(i);
					if (c == '<' || c == '>' || c == '&' || c == '"') {
						b.append(htmlStr.substring(next, i));
						switch (c) {
						case '<':
							b.append("&lt;");
							break;
						case '>':
							b.append("&gt;");
							break;
						case '&':
							b.append("&amp;");
							break;
						case '"':
							b.append("&quot;");
							break;
						}
						next = i + 1;
					}
					i++;
				}
				if (next < ln) {
					b.append(htmlStr.substring(next));
				}
				htmlStr = b.toString();
				break;
			} // if c ==
		} // for
		return htmlStr;
	}

	/**
	 * XML Encoding. Replaces all '&gt;' '&lt;' '&amp;', "'" and '"' with entity reference
	 *
	 * @param xmlStr
	 *            xml字符串
	 * @return 编译后的字符串
	 */
	public static String xmlEnc(String xmlStr) {
		int ln = xmlStr.length();
		for (int i = 0; i < ln; i++) {
			char c = xmlStr.charAt(i);
			if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
				StringBuffer b = new StringBuffer(xmlStr.substring(0, i));
				switch (c) {
				case '<':
					b.append("&lt;");
					break;
				case '>':
					b.append("&gt;");
					break;
				case '&':
					b.append("&amp;");
					break;
				case '"':
					b.append("&quot;");
					break;
				case '\'':
					b.append("&apos;");
					break;
				}
				i++;
				int next = i;
				while (i < ln) {
					c = xmlStr.charAt(i);
					if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
						b.append(xmlStr.substring(next, i));
						switch (c) {
						case '<':
							b.append("&lt;");
							break;
						case '>':
							b.append("&gt;");
							break;
						case '&':
							b.append("&amp;");
							break;
						case '"':
							b.append("&quot;");
							break;
						case '\'':
							b.append("&apos;");
							break;
						}
						next = i + 1;
					}
					i++;
				}
				if (next < ln) {
					b.append(xmlStr.substring(next));
				}
				xmlStr = b.toString();
				break;
			} // if c ==
		} // for
		return xmlStr;
	}

	/**
	 * XML encoding without replacing apostrophes and quotation marks.
	 *
	 * @param xmlStr
	 *            编译后的xml字符串
	 * @return 原始xml字符串
	 * @see #xmlEnc(String)
	 */
	public static String xmlEncNQ(String xmlStr) {
		int ln = xmlStr.length();
		for (int i = 0; i < ln; i++) {
			char c = xmlStr.charAt(i);
			if (c == '<' || c == '>' || c == '&') {
				StringBuffer b = new StringBuffer(xmlStr.substring(0, i));
				switch (c) {
				case '<':
					b.append("&lt;");
					break;
				case '>':
					b.append("&gt;");
					break;
				case '&':
					b.append("&amp;");
					break;
				}
				i++;
				int next = i;
				while (i < ln) {
					c = xmlStr.charAt(i);
					if (c == '<' || c == '>' || c == '&') {
						b.append(xmlStr.substring(next, i));
						switch (c) {
						case '<':
							b.append("&lt;");
							break;
						case '>':
							b.append("&gt;");
							break;
						case '&':
							b.append("&amp;");
							break;
						}
						next = i + 1;
					}
					i++;
				}
				if (next < ln) {
					b.append(xmlStr.substring(next));
				}
				xmlStr = b.toString();
				break;
			} // if c ==
		} // for
		return xmlStr;
	}

	/**
	 * 将字符串按照分割符分解成队列
	 *
	 * @param delimitedString
	 *            被分割的字符串
	 * @param delim
	 *            分割符
	 * @return List队列
	 */
	public static List<String> getDelimitedValues(String delimitedString, String delim) {
		List<String> delimitedValues = new ArrayList<String>();
		if (delimitedString != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(delimitedString, delim);
			while (stringTokenizer.hasMoreTokens()) {
				String nextToken = stringTokenizer.nextToken().toLowerCase().trim();
				if (nextToken.length() > 0) {
					delimitedValues.add(nextToken);
				}
			}
		}
		return delimitedValues;
	}

	/**
	 * 复写指定的字符串
	 *
	 * @param srcString
	 *            被复写的字符串
	 * @param numberOfCopies
	 *            需要复写的份数
	 * @return String 复写后的字符串
	 */
	public static String duplicate(String srcString, int numberOfCopies) {
		StringBuffer result = new StringBuffer();
		if (numberOfCopies > 0) {
			for (int i = 1; i <= numberOfCopies; i++) {
				result.append(srcString);
			}
		} else {
			result = new StringBuffer("");
		}
		return result.toString();
	}

	/**
	 * 强制转换字符串到long整型数字
	 *
	 * @param str
	 *            被转义的字符串
	 * @return long 转义后的long型数字，转换错误返回默认值 0
	 */
	public static long getLong(String str) {
		return getLong(str, 0);
	}

	public static long getLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception _ex) {
			return defaultValue;
		}
	}

	/**
	 * 强制转换字符串到int整型数字
	 *
	 * @param str
	 *            被转义的字符串
	 * @return int 转义后的int型数字，转换错误返回默认值 0
	 */
	public static int getInt(String str) {
		return getInt(str, 0);
	}

	public static int getInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception _ex) {
			return defaultValue;
		}
	}

	/**
	 * 强制转换字符串到Boolean型
	 *
	 * @param str
	 *            被转义的字符串
	 * @return int 转义后的boolean型值，字符串为none 或不为"true"时返回 false
	 */
	public static boolean getBoolean(String str) {
		return getBoolean(str, false);
	}

	/**
	 * 强制转换字符串到Boolean型
	 *
	 * @param str
	 *            被转义的字符串
	 * @param defaultValue
	 *            默认值
	 * @return boolean值， 转义后的boolean型值，字符串为none 或不为"true"时返回 false
	 */
	public static boolean getBoolean(String str, boolean defaultValue) {
		if (str == null) {
			return defaultValue;
		}

		if (VerifyerUnits.isDigit(str)) {
			int x = Integer.parseInt(str);
			if (x > 0) {
				return true;
			} else {
				return false;
			}
		}

		if (str.equals("true") || str.equals("yes")) {
			return true;
		}

		return false;
	}

	/**
	 * 对字符串进行校验检查
	 *
	 * @param srcString
	 *            需要校验检查的字符串
	 * @param validTarget
	 *            校验检查的目标字符集合
	 * @return int 源字符串中不符合目标字符集合的字符位置，校验成功返回-1
	 */
	public static int validString(String srcString, String validTarget) {
		for (int i = 0; i < srcString.length(); i++) {
			if (validTarget.indexOf(srcString.charAt(i)) == -1) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 判断是否中文字符
	 *
	 * @param string
	 *            被检测的字符串
	 * @return boolean 是中文字符返回true，否则返回false
	 */
	public static boolean hasChinese(String string) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(string);
		return m.find();
	}

	/**
	 * 计算字符串内某一个char的总数
	 *
	 * @param srcString
	 *            进行计算的字符串
	 * @param searchChar
	 *            检索目标char字符
	 * @return int 数量
	 */
	public static int charCount(String srcString, char searchChar) {
		int result = 0;
		if (srcString.length() > 0) {
			for (int i = 0; i < srcString.length(); i++) {
				if (searchChar == srcString.charAt(i)) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * 对字符串进行前段截除操作，截除起始符合目标的前段部分
	 *
	 * @param srcString
	 *            目标字符串
	 * @param token
	 *            被截除的标识字符串
	 * @return String 截除后的字符串
	 */
	public static String stripLeft(String srcString, String token) {
		if (srcString == null) {
			return null;
		}
		if (srcString.startsWith(token)) {
			return srcString.substring(token.length(), srcString.length());
		} else {
			return srcString;
		}
	}

	/**
	 * 对字符串进行截除操作，截除最后一个符合目标的尾部部分
	 *
	 * @param srcString
	 *            目标字符串
	 * @param token
	 *            被截除的标识字符串
	 * @return String 截除后的字符串
	 */
	public static String stripTrailing(String srcString, String token) {
		if (srcString == null) {
			return null;
		}
		if (srcString.endsWith(token)) {
			return srcString.substring(0, srcString.lastIndexOf(token));
		} else {
			return "";
		}
	}

	/**
	 * 对字符串前部进行截取
	 *
	 * @param srcString
	 *            目标字符串
	 * @param token
	 *            前部截取标志
	 * @return 截取后的字符串
	 */
	public static String splitLeft(String srcString, String token) {
		if (srcString == null) {
			return null;
		}
		int split = srcString.lastIndexOf(token);
		if (split == -1) {
			return srcString;
		}

		return srcString.substring(0, split);
	}

	/**
	 * 对字符串尾部进行截取
	 *
	 * @param srcString
	 *            目标字符串
	 * @param token
	 *            尾部截取标志
	 * @return 截取后的字符串
	 */
	public static String splitTrailing(String srcString, String token) {
		if (srcString == null) {
			return null;
		}
		int split = srcString.lastIndexOf(token);
		if (split == -1) {
			return "";
		}

		return srcString.substring(split + 1, srcString.length());
	}

	/**
	 * 对字符串进行Unicode编码
	 *
	 * @param srcString
	 *            进行编码的字符串
	 * @return String 编码后的字符串
	 */
	public static String encodeUnicode(String srcString) {
		if (srcString.trim().length() == 0) {
			return srcString;
		}
		StringBuffer encodedChars = new StringBuffer();
		for (int i = 0; i < srcString.length(); i++) {
			String charInHex = Integer.toString(srcString.charAt(i), 16).toUpperCase();
			switch (charInHex.length()) {
			case 1: // '\001'
				encodedChars.append("\\u000").append(charInHex);
				break;
			case 2: // '\002'
				encodedChars.append("\\u00").append(charInHex);
				break;
			case 3: // '\003'
				encodedChars.append("\\u0").append(charInHex);
				break;
			default:
				encodedChars.append("\\u").append(charInHex);
				break;
			}
		}
		return encodedChars.toString();
	}

	/**
	 * 获得字符串的字节数，有利于计算机字符串中英文混排后的实际长度
	 *
	 * @param srcString
	 *            进行计算的字符串
	 * @return int 字符串的byte长度
	 */
	public static int getByteLength(String srcString) {
		return getByteLength(srcString, "UTF-8");
	}

	/**
	 * 获得字符串的字节数，有利于计算机字符串中英文混排后的实际长度
	 *
	 * @param srcString
	 *            进行计算的字符串
	 * @param encode
	 *            字符串的编码
	 * @return int 字符串的byte长度
	 */
	public static int getByteLength(String srcString, String encode) {
		if (srcString == null) {
			return 0;
		}
		try {
			return srcString.getBytes(encode).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 截取字符串的前部，长度为BYTE数量。如果遇到双字节chat，则自动多截取一个字节。本方法仅支持双字节编码的字符串，例如UTF-8,ISO-8859-1,GBK
	 *
	 * @param strString
	 *            目标字符串
	 * @param length
	 *            截取的BYTE长度
	 * @return 截取获得的字符串
	 */
	public static String subStringBytes(String strString, int length) {
		if (getByteLength(strString) < length) {
			return strString;
		}

		// if the source string is not enough length, so return it back.
		int begin = 0;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strString.length(); i++) {
			char c = strString.charAt(i);
			if (c / 0x80 == 0) {
				begin += 1;
			} else {
				begin += 2;
			}
			if (begin > length) {
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 转换ISO8859-1字符集到GBK字符集
	 *
	 * @param str
	 *            ISO8859-1字符串
	 * @return String GBK编码字符串
	 */
	public static String ISO2GBK(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换ISO8859-1字符集到BIG5字符集
	 *
	 * @param str
	 *            ISO8859-1字符串
	 * @return String BIG5编码字符串
	 */
	public static String ISO2BIG(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("ISO-8859-1"), "BIG5");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换ISO8859-1字符集到UTF-8字符集
	 *
	 * @param str
	 *            ISO8859-1字符串
	 * @return String UTF-8编码字符串
	 */
	public static String ISO2UTF(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换GBK字符集到ISO8859-1字符集
	 *
	 * @param str
	 *            GBK编码字符串
	 * @return String ISO8859-1字符串
	 */
	public static String GBK2ISO(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换GBK字符集到BIG5字符集
	 *
	 * @param str
	 *            GBK编码字符串
	 * @return String BIG5字符串
	 */
	public static String GBK2BIG(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("GBK8"), "BIG5");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换GBK字符集到UTF-8字符集
	 *
	 * @param str
	 *            GBK编码字符串
	 * @return String UTF-8字符串
	 */
	public static String GBK2UTF(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("GBK"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换UTF-8字符集ISO-8859-1字符集
	 *
	 * @param str
	 *            UTF-8编码字符串
	 * @return String ISO-8859-1字符串
	 */
	public static String UTF2ISO(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换UTF-8字符集GBK字符集
	 *
	 * @param str
	 *            UTF-8编码字符串
	 * @return String GBK字符串
	 */
	public static String UTF2GBK(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("UTF-8"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换UTF-8字符集BIG5字符集
	 *
	 * @param str
	 *            UTF-8编码字符串
	 * @return String BIG5字符串
	 */
	public static String UTF2BIG(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("UTF-8"), "BIG5");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换BIG5字符集ISO-8859-1字符集
	 *
	 * @param str
	 *            BIG5编码字符串
	 * @return String ISO-8859-1字符串
	 */
	public static String BIG2ISO(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("BIG5"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换BIG5字符集UTF-8字符集
	 *
	 * @param str
	 *            BIG5编码字符串
	 * @return String UTF-8字符串
	 */
	public static String BIG2UTF(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("BIG5"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 转换BIG5字符集GBK字符集
	 *
	 * @param str
	 *            BIG5编码字符串
	 * @return String GBK字符串
	 */
	public static String BIG2GBK(String str) {
		if (str == null) {
			return null;
		}
		try {
			return new String(str.getBytes("BIG5"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 对字符串进行遍历转换
	 *
	 * @param str
	 *            编码字符串
	 * @return String 以ISO-8859-1,GBK,UTF-8,BIG5四种编码转换后的字符串样例
	 * @throws UnsupportedEncodingException
	 *             不支持的编码格式
	 */
	public static String testEncode(String str) throws UnsupportedEncodingException {
		StringBuffer buf = new StringBuffer();
		buf.append("ISO-8859-1 to UTF-8      -->").append(new String(str.getBytes("ISO-8859-1"), "UTF-8")).append("\n");
		buf.append("ISO-8859-1 to GBK        -->").append(new String(str.getBytes("ISO-8859-1"), "GBK")).append("\n");
		buf.append("ISO-8859-1 to GB2312     -->").append(new String(str.getBytes("ISO-8859-1"), "GB2312")).append("\n");
		buf.append("ISO-8859-1 to BIG5       -->").append(new String(str.getBytes("ISO-8859-1"), "BIG5")).append("\n");
		buf.append("GBK        to UTF-8      -->").append(new String(str.getBytes("GBK"), "UTF-8")).append("\n");
		buf.append("GBK        to ISO-8859-1 -->").append(new String(str.getBytes("GBK"), "ISO-8859-1")).append("\n");
		buf.append("GBK        to GB2312     -->").append(new String(str.getBytes("GBK"), "GB2312")).append("\n");
		buf.append("GBK        to BIG5       -->").append(new String(str.getBytes("GBK"), "BIG5")).append("\n");
		buf.append("UTF-8      to ISO-8859-1 -->").append(new String(str.getBytes("UTF-8"), "ISO-8859-1")).append("\n");
		buf.append("UTF-8      to GBK        -->").append(new String(str.getBytes("UTF-8"), "GBK")).append("\n");
		buf.append("UTF-8      to GB2312     -->").append(new String(str.getBytes("UTF-8"), "GB2312")).append("\n");
		buf.append("UTF-8      to BIG5       -->").append(new String(str.getBytes("UTF-8"), "BIG5")).append("\n");

		return buf.toString();
	}

	/**
	 * 对字符串进行长度格式操作,对操作指定长度的字符串进行缩略。如：I have to take...
	 *
	 * @param str
	 *            需要格式化的字符串
	 * @param maxLength
	 *            格式化后的字符串最大长度
	 * @return String 格式化后的字符串
	 * @since 1.4.6
	 */
	public static String abbreviate(String str, int maxLength) {
		if (str == null)
			return "";

		// if the source string is not enough length, so return it back.
		int suffixLength = ABBREVIATE_SUFFIX.length();
		if (str.getBytes().length < maxLength)
			return str;

		int newLength = maxLength - suffixLength;
		int begin = 0;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			sb.append(c);
			if (c / 0x80 == 0) {
				begin += 1;
			} else {
				begin += 2;
			}
			if (begin >= newLength) {
				break;
			}
		}
		return sb.append(ABBREVIATE_SUFFIX).toString();
	}

	public static String upperCaseFirst(String s) {
		if (s == null || s.length() < 1) {
			return s;
		}

		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * <p>
	 * Returns padding using the specified delimiter repeated to a given length.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.repeat(0, 'e')  = ""
	 * StringUtils.repeat(3, 'e')  = "eee"
	 * StringUtils.repeat(-2, 'e') = ""
	 * </pre>
	 * <p>
	 * Note: this method doesn't not support padding with <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
	 * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to support full I18N of your applications consider using
	 * {@link #repeat(String, int)} instead.
	 * </p>
	 *
	 * @param ch
	 *            character to repeat
	 * @param repeat
	 *            number of times to repeat char, negative treated as zero
	 * @return String with repeated character
	 * @see #repeat(String, int)
	 */
	public static String repeat(char ch, int repeat) {
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}

	/**
	 * <p>
	 * Repeat a String {@code repeat} times to form a new String.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.repeat(null, 2) = null
	 * StringUtils.repeat("", 0)   = ""
	 * StringUtils.repeat("", 2)   = ""
	 * StringUtils.repeat("a", 3)  = "aaa"
	 * StringUtils.repeat("ab", 2) = "abab"
	 * StringUtils.repeat("a", -2) = ""
	 * </pre>
	 *
	 * @param str
	 *            the String to repeat, may be null
	 * @param repeat
	 *            number of times to repeat str, negative treated as zero
	 * @return a new String consisting of the original String repeated, {@code null} if null String input
	 */
	public static String repeat(String str, int repeat) {
		// Performance tuned for 2.0 (JDK1.4)

		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return EMPTY;
		}
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) {
			return str;
		}
		if (inputLength == 1 && repeat <= PAD_LIMIT) {
			return repeat(str.charAt(0), repeat);
		}

		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1:
			return repeat(str.charAt(0), repeat);
		case 2:
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char[] output2 = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
			}
			return new String(output2);
		default:
			StringBuilder buf = new StringBuilder(outputLength);
			for (int i = 0; i < repeat; i++) {
				buf.append(str);
			}
			return buf.toString();
		}
	}

	public static Set<String> toSet(String source, String regex) {
		Set<String> set = new HashSet<>();
		if (StringUtils.isEmpty(source))
			return set;

		for(String sub : source.split(regex)) {
			set.add(sub.trim());
		}
		return set;
	}

	public static List<String> toList(String source, String regex) {
		List<String> set = new ArrayList<>();
		if (StringUtils.isEmpty(source))
			return set;

		for(String sub : source.split(regex)) {
			set.add(sub.trim());
		}
		return set;
	}

	public static String[] toArray(String source, String regex) {
		List<String> set = new ArrayList<>();
		if (StringUtils.isEmpty(source))
			return set.toArray(new String[0]);

		for(String sub : source.split(regex)) {
			set.add(sub.trim());
		}
		return set.toArray(new String[0]);
	}

	public static String underlineToCamel(String source) {
		String temp = source.toLowerCase();
		int len = temp.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = temp.charAt(i);
			if (c == '_') {
				if (++i < len) {
					sb.append(Character.toUpperCase(temp.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String camelToUnderline(String param) {
		if (isBlank(param)) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append('_');
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	public static String utilsNotEmpty(String...strings) {
		for(String value: strings) {
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(value))
				return value;
		}
		return "";
	}

}
