package com.wisdge.web;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import com.wisdge.utils.StringUtils;

public class IPUtils {
	/**
	 * 获取远端真实IP
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return String ipAddress
	 */
	public static String getRemoteIP(HttpServletRequest request) {
		String ip = request.getHeader("x-real-ip");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (!StringUtils.isEmpty(ip))
			ip = ip.split(",")[0];

		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	/**
	 * 将IP转成10进制整数
	 */
	public static long ipToLong(String strIP) {
		long[] ip = new long[4];
		int position1 = strIP.indexOf(".");
		int position2 = strIP.indexOf(".", position1 + 1);
		int position3 = strIP.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(strIP.substring(0, position1));
		ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIP.substring(position3 + 1));
		// ip1*256*256*256+ip2*256*256+ip3*256+ip4
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	/**
	 * 将10进制整数形式转换成127.0.0.1形式的IP地址
	 */
	public static String longToIP(long longIP) {
		StringBuilder sb = new StringBuilder("");
		// 直接右移24位
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	/**
	 * 是否是本地IP
	 *
	 * @param strIp
	 * @return boolean
	 */
	public static boolean isLocal(String strIp) {
		if ("127.0.0.1".equals(strIp))
			return true;
		long l = ipToLong(strIp);
		if (l >= 3232235520L)
			return l <= 3232301055L;
		return (l >= 167772160L) && (l <= 184549375L);
	}

	public static List<String> getLocalIPs() throws SocketException {
		List<String> ips = new ArrayList<String>();
		Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = allNetInterfaces.nextElement();
			// System.out.println(netInterface.getName());
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = addresses.nextElement();
				if (ip != null && ip instanceof Inet4Address) {
					String ipStr = ip.getHostAddress();
					if (isIp(ipStr))
						ips.add(ipStr);
				}
			}
		}
		return ips;
	}

	/**
	 * IP格式校验
	 *
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isIp(String str) {
		Pattern pattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * @return 本机主机名
	 */
	public static String getHostName() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (ia == null) {
			return "some error..";
		} else
			return ia.getHostName();
	}

	/**
	 * 获取当前操作系统名称
	 * @return 操作系统名称，例如: windows, linux
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	// IP的正则
	private static final Pattern pattern = Pattern.compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\." + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");
	private static final String DEFAULT_ALLOW_ALL_FLAG = "*";// 允许所有ip标志位
	private static final String DEFAULT_DENY_ALL_FLAG = "0"; // 禁止所有ip标志位

	/**
	 * isPermited: (根据IP地址，及IP白名单设置规则判断IP是否包含在白名单).
	 * @param ip String 需要验证的IP地址
	 * @param ipWhiteConfigs Set<String> 验证格式队列
	 */
	public static boolean isPermited(String ip, Set<String> ipWhiteConfigs) {
		if (StringUtils.isEmpty(ip))
			return false;
		if (!pattern.matcher(ip).matches())
			return false;

		for(String ipWhiteConfig: ipWhiteConfigs) {
			if (_isPermited(ip, ipWhiteConfig))
				return true;
		}
		return false;
	}

	/**
	 * isPermited: (根据IP地址，及IP白名单设置规则判断IP是否包含在白名单).
	 * @param ip String 需要验证的IP地址
	 * @param ipWhiteConfig String 验证格式
	 */
	public static boolean isPermited(String ip, String ipWhiteConfig) {
		if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(ipWhiteConfig))
			return false;

		if (!pattern.matcher(ip).matches())
			return false;

		if (DEFAULT_ALLOW_ALL_FLAG.equals(ipWhiteConfig))
			return true;
		if (DEFAULT_DENY_ALL_FLAG.equals(ipWhiteConfig))
			return false;

		return _isPermited(ip, ipWhiteConfig);
	}

	private static boolean _isPermited(String ip, String ipWhiteConfig) {
		if (StringUtils.isEmpty(ipWhiteConfig))
			return false;
		if (ip.equals(ipWhiteConfig))
			return true;

		if (ipWhiteConfig.indexOf("-") > -1) {// 处理 类似 192.168.0.0-192.168.2.1
			String[] tempAllow = ipWhiteConfig.split("-");
			String[] from = tempAllow[0].split("\\.");
			String[] end = tempAllow[1].split("\\.");
			String[] tag = ip.split("\\.");
			boolean check = true;
			for (int i = 0; i < 4; i++) {// 对IP从左到右进行逐段匹配
				int s = Integer.valueOf(from[i]);
				int t = Integer.valueOf(tag[i]);
				int e = Integer.valueOf(end[i]);
				if (!(s <= t && t <= e)) {
					check = false;
					break;
				}
			}
			if (check)
				return true;
		} else if (ipWhiteConfig.contains("/")) {// 处理 网段 xxx.xxx.xxx./24
			int splitIndex = ipWhiteConfig.indexOf("/");
			// 取出子网段
			String ipSegment = ipWhiteConfig.substring(0, splitIndex); // 192.168.3.0
			// 子网数
			String netmask = ipWhiteConfig.substring(splitIndex + 1);// 24
			// ip 转二进制
			long ipLong = ipToLong(ip);
			// 子网二进制
			long maskLong = (2L << 32 - 1) - (2L << Integer.valueOf(32 - Integer.valueOf(netmask)) - 1);
			// ip与和子网相与 得到 网络地址
			String calcSegment = longToIP(ipLong & maskLong);
			// 如果计算得出网络地址和库中网络地址相同 则合法
			if (ipSegment.equals(calcSegment))
				return true;
		} else if (ipWhiteConfig.contains("*")) {// 处理通配符 *
			String[] ips = ipWhiteConfig.split("\\.");
			String[] from = new String[] { "0", "0", "0", "0" };
			String[] end = new String[] { "255", "255", "255", "255" };
			List<String> temp = new ArrayList<>();
			for (int i = 0; i < ips.length; i++) {
				if (ips[i].indexOf("*") > -1) {
					temp = complete(ips[i]);
					from[i] = null;
					end[i] = null;
				} else {
					from[i] = ips[i];
					end[i] = ips[i];
				}
			}

			StringBuilder fromIP = new StringBuilder();
			StringBuilder endIP = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				if (from[i] != null) {
					fromIP.append(from[i]).append(".");
					endIP.append(end[i]).append(".");
				} else {
					fromIP.append("[*].");
					endIP.append("[*].");
				}
			}

			fromIP.deleteCharAt(fromIP.length() - 1);
			endIP.deleteCharAt(endIP.length() - 1);

			for (String s : temp) {
				String vip = fromIP.toString().replace("[*]", s.split(";")[0]) + "-" + endIP.toString().replace("[*]", s.split(";")[1]);
				if (validate(vip) && _isPermited(ip, vip)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 对单个IP节点进行范围限定
	 * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
	 */
	private static List<String> complete(String arg) {
		List<String> com = new ArrayList<>();
		int len = arg.length();
		if (len == 1) {
			com.add("0;255");
		} else if (len == 2) {
			String s1 = complete(arg, 1);
			if (s1 != null)
				com.add(s1);
			String s2 = complete(arg, 2);
			if (s2 != null)
				com.add(s2);
		} else {
			String s1 = complete(arg, 1);
			if (s1 != null)
				com.add(s1);
		}
		return com;
	}

	private static String complete(String arg, int length) {
		String from = "";
		String end = "";
		if (length == 1) {
			from = arg.replace("*", "0");
			end = arg.replace("*", "9");
		} else {
			from = arg.replace("*", "00");
			end = arg.replace("*", "99");
		}
		if (Integer.valueOf(from) > 255)
			return null;
		if (Integer.valueOf(end) > 255)
			end = "255";
		return from + ";" + end;
	}

	/**
	 * 在添加至白名单时进行格式校验
	 *
	 * @param ip
	 * @return
	 */
	private static boolean validate(String ip) {
		String[] temp = ip.split("-");
		for (String s : temp)
			if (!pattern.matcher(s).matches()) {
				return false;
			}
		return true;
	}
}
