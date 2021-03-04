package com.wisdge.web;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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
	 * IP转成数字类型
	 * 
	 * @param strIP
	 * @return long
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

	@Test
	public void test() throws SocketException {
		List<String> ips = IPUtils.getLocalIPs();
		System.out.println(ips);

		System.out.println("OS: " + getOSName());
		System.out.println("HostName: " + getHostName());
	}
}
