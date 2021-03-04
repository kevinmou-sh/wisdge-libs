package com.wisdge.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.lang3.StringUtils;

/**
 * Extra Map class<br>
 * Extends HashMap, appending a lot of methods, include toString(formated) etc.
 * 
 * @author Kevin MOU
 * @version 1.1
 */
public class ExtraMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -1L;

	public ExtraMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	public ExtraMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ExtraMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ExtraMap() {
		super();
	}

	/**
	 * 根据KEY取相应的值，如KEY不存在，则返回默认值
	 * 
	 * @param key
	 *            主键
	 * @param defaultValue
	 *            默认值
	 * @return Object对象
	 */
	public Object getObjectValue(Object key, Object defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		return get(key);
	}

	/**
	 * 取值并强制转换为String
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return String
	 */
	public String getStringValue(Object key, String defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object value = get(key);
		if (value instanceof String) {
			return (String) value;
		} else {
			return value.toString();
		}
	}

	/**
	 * 取值并强制转换为boolean
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return boolean
	 */
	public boolean getBooleanValue(Object key, boolean defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue();
		} else if (obj instanceof Integer) {
			return ((Integer) obj).intValue() > 0;
		} else if (obj instanceof String) {
			return ((String) obj).equalsIgnoreCase("true") || ((String) obj).equalsIgnoreCase("yes");
		} else {
			return defaultValue;
		}
	}

	/**
	 * 取值并强制转换为byte
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return byte
	 */
	public byte getByteValue(Object key, byte defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Byte) {
			return ((Byte) obj).byteValue();
		} else if (obj instanceof Byte[] && ((Byte[]) obj).length > 0) {
			return ((Byte[]) obj)[0];
		} else if (obj instanceof String) {
			return Byte.parseByte((String) obj);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 取值并强制转换为char
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return char
	 */
	public char getCharValue(Object key, char defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof String && ((String) obj).length() > 0) {
			return ((String) obj).charAt(0);
		} else {
			return defaultValue;
		}
	}

	private double getDouble(String obj) throws NumberFormatException {
		return (new Double(obj)).doubleValue();
	}

	/**
	 * 取值并强制转换为double
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return double
	 */
	public double getDoubleValue(Object key, double defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Double) {
			return ((Double) obj).doubleValue();
		} else if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
			return getDouble((String) obj);
		} else {
			return defaultValue;
		}
	}

	private float getFloat(String obj) throws NumberFormatException {
		return (new Float(obj)).floatValue();
	}

	/**
	 * 取值并强制转换为float
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return float
	 */
	public float getFloatValue(Object key, float defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Double) {
			return ((Float) obj).floatValue();
		} else if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
			return getFloat((String) obj);
		} else {
			return defaultValue;
		}
	}

	private int getInt(String obj) throws NumberFormatException {
		return Integer.parseInt(obj);
	}

	/**
	 * 取值并强制转换为int
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return int
	 */
	public int getIntValue(Object key, int defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Integer) {
			return ((Integer) obj).intValue();
		} else if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
			return getInt((String) obj);
		} else {
			return defaultValue;
		}
	}

	private long getLong(String obj) throws NumberFormatException {
		return Long.parseLong(obj);
	}

	/**
	 * 取值并强制转换为long
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return long
	 */
	public long getLongValue(Object key, long defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Long) {
			return ((Long) obj).longValue();
		} else if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
			return getLong((String) obj);
		} else {
			return defaultValue;
		}
	}

	private short getShort(String obj) throws NumberFormatException {
		return Short.parseShort(obj);
	}

	/**
	 * 取值并强制转换为short
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return short
	 */
	public short getShortValue(Object key, short defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}

		Object obj = get(key);
		if (obj instanceof Short) {
			return ((Short) obj).shortValue();
		} else if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
			return getShort((String) obj);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 取出遗失的键值
	 * 
	 * @param keys
	 *            键数组
	 * @return String[] 请求的键中遗失的部分
	 */
	public String[] getMissingValues(Object keys[]) {
		Vector<Object> missing = new Vector<Object>();
		for (int i = 0; i < keys.length; i++) {
			if (!containsKey(keys[i])) {
				missing.addElement(keys[i]);
			}
		}

		if (missing.size() == 0) {
			return null;
		} else {
			String ret[] = new String[missing.size()];
			missing.copyInto(ret);
			return ret;
		}
	}

}
