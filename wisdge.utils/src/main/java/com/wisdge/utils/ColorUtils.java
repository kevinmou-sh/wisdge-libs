package com.wisdge.utils;

/**
 * @author Kevin MOU
 * @version 1.0
 */
public class ColorUtils {

	public static String int2hex(int color) {
		int r = color & 255;
		int g = (color>>8) & 255;
		int b = (color>>16) & 255;
		return int2hex(new int[] {r, g, b});
	}

	/**
	 * Get HEX string from int[]
	 * 
	 * @param int[]
	 *            RGB int[]
	 * @return HEX string
	 */
	public static String int2hex(int[] rgb) {
		String hex = Integer.toHexString((rgb[0] << 16 | rgb[1] << 8 | rgb[2]) & 0x00ffffff);
		int pads = 6 - hex.length();
		return StringUtils.repeat('0', pads).concat(hex);
	}
	
	/**
	 * Get color byte[] from hex string
	 * 
	 * @param colorHex
	 *            HEX string
	 * @return byte[] byte RED, byte GREEN, byte BLUE
	 */
	public static byte[] hex2byte(String colorHex) {
		if (colorHex.startsWith("#")) {
			colorHex = colorHex.substring(1);
		}
		int hex = Integer.valueOf(colorHex, 16);
		byte r = (byte) ((0xff << 16 & hex) >> 16);
		byte g = (byte) ((0xff << 8 & hex) >> 8);
		byte b = (byte) (0xff & hex);
		return new byte[]{r, g, b};
	}
	
	/**
	 * Get color int[] from hex string
	 * 
	 * @param colorHex
	 *            HEX string
	 * @return int[] int RED, int GREEN, int BLUE
	 */
	public static int[] hex2int(String colorHex) {
		if (colorHex.startsWith("#")) {
			colorHex = colorHex.substring(1);
		}
		int hex = Integer.valueOf(colorHex, 16);
		int r = ((0xff << 16 & hex) >> 16);
		int g = ((0xff << 8 & hex) >> 8);
		int b = (0xff & hex);
		return new int[]{r, g, b};
	}
	
	public static int hex2index(String colorHex) {
		if (colorHex.startsWith("#")) {
			colorHex = colorHex.substring(1);
		}
		int hex = Integer.valueOf(colorHex, 16);
		int r = ((0xff << 16 & hex) >> 16);
		int g = ((0xff << 8 & hex) >> 8);
		int b = (0xff & hex);
		return (b << 16) | (g << 8) | r;
	}

}
