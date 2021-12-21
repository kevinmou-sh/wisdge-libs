package com.wisdge.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 时间日期处理类
 * 
 * @version 1.0.0.20120410
 * @author KevinMOU
 */
public class DateUtils {
	private static final Log logger = LogFactory.getLog(DateUtils.class);

	public final static String ISO8601ShortPattern = "yyyy-MM-dd";
	public final static String ISO8601ShortPatternWithDay = "yyyy-MM-dd E";
	public final static String ISO8601LongPattern = "yyyy-MM-dd HH:mm:ss";
	public final static String ISO8601LongPatternWithDay = "yyyy-MM-dd HH:mm:ss E";
	public final static String ISO8601LongestPattern = "yyyy-MM-dd HH:mm:ss.S";
	public final static String ISO8601SlatPattern = "yyyy/MM/dd HH:mm:ss";
	public final static String ISO8601SlatPatternWithDay = "yyyy/MM/dd HH:mm:ss E";
	public final static String ISO8601TimePattern = "HH:mm:ss";
	public final static String ISO8601ChineseLongPattern = "yyyy年MM月dd日 HH时mm分ss秒";
	public final static String ISO8601ChineseLongPatternWithDay = "yyyy年MM月dd日 HH时mm分ss秒 E";
	public final static String ISO8601ChineseShortPattern = "yyyy年MM月dd日";
	public final static String ISO8601ChineseShortPatternWithDay = "yyyy年MM月dd日 E";

	/**
	 * 获得某一个指定时间的当天起始时间和结束时间。<br>
	 * 例： date = 1993-09-23 4:23:65,445 -&gt; begin = 1993-09-23 0:0:0,000 -&gt; end = 1993-09-23 23:59:59,999
	 * 
	 * @param date
	 *            java.util.Date 被指定的时间
	 * @return java.util.Date[] value[0]=该日早晨零时， value[1]=该日夜晚零时
	 */
	public static Date[] getDayPeriod(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date begin = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date end = cal.getTime();
		return new Date[] { begin, end };
	}

	/**
	 * 获得某一个指定时间的当周起始时间和结束时间.
	 * 
	 * @param date
	 *            java.util.Date 被指定的时间
	 * @return java.util.Date[] value[0]=周一早晨零时， value[1]=周日夜晚零时
	 */
	public static Date[] getWeekPeriod(Date date) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DATE, -1);
		}
		Date begin = cal.getTime();

		cal.add(Calendar.DATE, 6);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date end = cal.getTime();

		return new Date[] { begin, end };
	}

	/**
	 * 获得某一个指定时间的当月起始时间和结束时间.
	 * 
	 * @param date
	 *            java.util.Date 被指定的时间
	 * @return java.util.Date[] value[0]=月初1号早晨零时，java.util.Date[] value[1]=月末最后一日夜晚零时
	 */
	public static Date[] getMonthPeriod(Date date) {
		if (date == null) {
			return null;
		}

		int[] days = { 30, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date begin = cal.getTime();

		boolean leapyear = cal.get(Calendar.YEAR) % 4 == 0 ? true : false;
		int month = cal.get(Calendar.MONTH);
		if (month == 1 && leapyear) {
			cal.set(Calendar.DAY_OF_MONTH, days[month] + 1);
		} else {
			cal.set(Calendar.DAY_OF_MONTH, days[month]);
		}
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date end = cal.getTime();

		return new Date[] { begin, end };
	}

	/**
	 * 判断两个时间是否同一天
	 * 
	 * @param first
	 *            进行判断的目标时间1
	 * @param second
	 *            进行判断的目标时间2
	 * @return boolean 相同的日期返回true, 反之则返回false
	 */
	public static boolean isSameDay(Date first, Date second) {
		Calendar calA = Calendar.getInstance();
		calA.setTime(first);
		Calendar calB = Calendar.getInstance();
		calB.setTime(second);
		if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断两个日期是否同一周
	 * 
	 * @param first
	 *            进行判断的目标时间1
	 * @param second
	 *            进行判断的目标时间2
	 * @return boolean 相同的日期返回true, 反之则返回false
	 */
	public static boolean isSameWeek(Date first, Date second) {
		Calendar calA = Calendar.getInstance();
		calA.setTime(first);
		Calendar calB = Calendar.getInstance();
		calB.setTime(second);
		if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.WEEK_OF_YEAR) == calB.get(Calendar.WEEK_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断两个日期是否同一月
	 * 
	 * @param first
	 *            进行判断的目标时间1
	 * @param second
	 *            进行判断的目标时间2
	 * @return boolean 相同的日期返回true, 反之则返回false
	 */
	public static boolean isSameMonth(Date first, Date second) {
		Calendar calA = Calendar.getInstance();
		calA.setTime(first);
		Calendar calB = Calendar.getInstance();
		calB.setTime(second);
		if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Date型日期转换成字符串
	 * 
	 * @param date
	 *            进行转换的日期Date对象
	 * @return String 转换后的字符串
	 */
	public static String format(Date date) {
		return format(date, ISO8601LongPattern);
	}
	
	public static String format(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		if (date == null)
			return "";
		
		return simpleDateFormat.format(date);
	}

	/**
	 * Date型日期转换成字符串, 并且按照规定格式输出
	 * 
	 * @param date
	 *            进行转换的日期
	 * @param day
	 *            是否输出星期
	 * @param time
	 *            是否输出时间
	 * @param locale
	 *            输出字符集区域
	 * @return String 转换后的字符串
	 */
	public static String format(Date date, boolean day, boolean time, Locale locale) {
		if (date == null) {
			return "&nbsp;";
		}
		String WEEK_CN[] = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		String WEEK_EN[] = new String[] { "Sun", "Mon.", "Tues.", "Wed", "Thurs", "Fri", "Sat" };
		String s = "";
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		if (locale.equals(Locale.CHINA)) {
			s = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日";
			if (day) {
				s += " " + WEEK_CN[cal.get(Calendar.DAY_OF_WEEK) - 1];
			}
		} else {
			s = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
			if (day) {
				s += " " + WEEK_EN[cal.get(Calendar.DAY_OF_WEEK) - 1];
			}
		}
		if (time) {
			try {
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				int min = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);
				String sh = Integer.toString(hour), sm = Integer.toString(min), ss = Integer.toString(second);
				if (sh.length() < 2) {
					sh = "0" + sh;
				}
				if (sm.length() < 2) {
					sm = "0" + sm;
				}
				if (ss.length() < 2) {
					ss = "0" + ss;
				}
				if (locale.equals(Locale.CHINA))
					s += " " + sh + "点" + sm + "分" + ss + "秒";
				else
					s += " " + sh + ":" + sm + ":" + ss;
			} catch (Exception e) {
				logger.error(e);
				// e.printStackTrace();
			}
		}
		return s;
	}
	
	public static Date parse(String source) throws ParseException {
		return parse(source, ISO8601LongPattern);
	}
	
	public static Date parse(String source, String pattern) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.parse(source);
	}

	/**
	 * 对两个日期进行比较
	 * 
	 * @param first
	 *            进行判断的目标时间1
	 * @param second
	 *            进行判断的目标时间1
	 * @return int 比较结果
	 */
	public static int compare(Date first, Date second) {
		Calendar source = new GregorianCalendar();
		source.setTime(first);
		Calendar target = new GregorianCalendar();
		target.setTime(second);
		return source.compareTo(target);
	}
}
