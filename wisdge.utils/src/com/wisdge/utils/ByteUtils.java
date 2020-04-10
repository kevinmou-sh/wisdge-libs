package com.wisdge.utils;

/**
 * @author Kevin MOU
 * @version 0.13
 */
public class ByteUtils {
	/**
	 * 重新设定字节数组的长度，不改变字节数组内原有的数据。
	 * 
	 * @param array
	 *            原始字节数组.
	 * @param newLength
	 *            被改变后的长度.
	 * @return 新的字节数组，如果新数组长度短于原数组，则差额部分的数据被截除。
	 */
	public static byte[] resizeByteArray(byte[] array, int newLength) {
		byte[] newArray = new byte[newLength];
		int oldLength = array.length;
		System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
		return newArray;
	}

	/**
	 * 在指定的位置向目标节数组写入一段新的字节数据.
	 * 
	 * @param source
	 *            目标数据
	 * @param data
	 *            被写入目标数据的字节数组.
	 * @param offset
	 *            开始写入的位置.
	 * @return 写入完成后的位置
	 */
	public static int writeBytes(byte[] source, byte[] data, int offset) {
		if (data.length + offset > source.length) {
			source = resizeByteArray(source, data.length + offset);
		}
		System.arraycopy(data, 0, source, offset, data.length);
		return offset + data.length;
	}

	/**
	 * 从一个byte[]数组中截取一部分
	 * 
	 * @param source
	 *            源数据
	 * @param start
	 *            截取开始的位置
	 * @param lenght
	 *            截取的长度
	 * @return byte[]
	 */
	public static byte[] subBytes(byte[] source, int start, int length) {
		byte[] data = new byte[length];
		System.arraycopy(source, start, data, 0, length);
		return data;
	}

	/**
	 * 整数到字节数组的转换
	 * 
	 * @param number
	 *            整数
	 * @return byte[] 字节数组
	 */
	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	/**
	 * 字节数组到整数的转换
	 * 
	 * @param b
	 *            字节数组
	 * @return int 整数
	 */
	public static int byteToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[i] >= 0) {
				s = s + b[i];
			} else {
				s = s + 256 + b[i];
			}
			s = s * 256;
		}
		if (b[3] >= 0) {
			s = s + b[3];
		} else {
			s = s + 256 + b[3];
		}
		return s;
	}

	/**
	 * 字符到字节转换
	 * 
	 * @param ch
	 *            字符
	 * @return byte[] 字节数组
	 */
	public static byte[] charToByte(char ch) {
		int temp = ch;
		byte[] b = new byte[2];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	/**
	 * 字节到字符转换
	 * 
	 * @param b
	 *            字节数组
	 * @return char 字符
	 */
	public static char byteToChar(byte[] b) {
		int s = 0;
		if (b[0] > 0) {
			s += b[0];
		} else {
			s += 256 + b[0];
		}
		s *= 256;
		if (b[1] > 0) {
			s += b[1];
		} else {
			s += 256 + b[1];
		}
		char ch = (char) s;
		return ch;
	}

	/**
	 * 浮点到字节转换
	 * 
	 * @param d
	 *            浮点数
	 * @return byte[] 字节数组
	 */
	public static byte[] doubleToByte(double d) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(d);
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;

		}
		return b;
	}

	/**
	 * 字节到浮点转换
	 * 
	 * @param b
	 *            字节数组
	 * @return double 浮点数
	 */
	public static double byteToDouble(byte[] b) {
		long l;

		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;

		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);

		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
	}

}
