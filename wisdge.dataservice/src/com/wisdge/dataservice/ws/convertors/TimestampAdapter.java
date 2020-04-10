package com.wisdge.dataservice.ws.convertors;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * java.sql.Timestamp类型转换
 * 
 * <pre>
 * 在web service接口中，获得的日期戳对象需要和字符串进行序列化转换。
 * </pre>
 * 
 * @author Kevin MOU
 * @version 1.0.0.20120410
 * @see MapAdapter
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
	/*
	 * (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	public String marshal(Timestamp time) throws Exception {
		return timestamp2Str(time);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	public Timestamp unmarshal(String str) throws Exception {
		return str2Timestamp(str);
	}

	/**
	 * 时间戳转换为字符串
	 * 
	 * @param time
	 *            时间戳
	 * @return 转化后的时间戳字符串
	 * @see #str2Timestamp(String)
	 */
	public static String timestamp2Str(Timestamp time) {
		Date date = new Date(time.getTime());
		return date2Str(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 字符串转换为时间戳对象
	 * 
	 * @param str
	 *            时间戳字符串
	 * @return 转化后的时间戳对象
	 * @see #timestamp2Str(Timestamp)
	 */
	public static Timestamp str2Timestamp(String str) {
		Date date = str2Date(str, "yyyy-MM-dd HH:mm:ss");
		return new Timestamp(date.getTime());
	}

	/**
	 * 转换日期对象到字符串
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            日期格式
	 * @return 字符串
	 * @see #str2Date(String, String)
	 */
	public static String date2Str(Date date, String format) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 字符串转换成日期，如果转换格式为空，则利用默认格式进行转换操作
	 * 
	 * @param str
	 *            字符串
	 * @param format
	 *            日期格式
	 * @return 日期
	 * @see #date2Str(Date, String)
	 */
	public static Date str2Date(String str, String format) {
		if (null == str || "".equals(str)) {
			return null;
		}
		// 如果没有指定字符串转换的格式，则用默认格式进行转换
		if (null == format || "".equals(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

}