package com.wisdge.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.codec.binary.Base64;

/**
 * 对字符串进行压缩和解压缩
 * 
 * @author Kevin MOU
 * @version 1.0.0.20130105
 */
public class CompressUtils {
	private static int cachesize = 1024;
	private static Inflater decompresser = new Inflater();
	private static Deflater compresser = new Deflater();

	/**
	 * Compress from byte[]
	 * 
	 * @param input
	 *            uncompress bytes source
	 * @return compressed bytes
	 * @see #decompressBytes(byte[])
	 */
	public static byte[] compressBytes(byte input[]) {
		compresser.reset();
		compresser.setInput(input);
		compresser.finish();
		byte output[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			int got;
			while (!compresser.finished()) {
				got = compresser.deflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * Decompress from bytes
	 * 
	 * @param input
	 *            compressed source bytes
	 * @return Uncompress bytes
	 * @see #compressBytes(byte[])
	 */
	public static byte[] decompressBytes(byte input[]) {
		byte output[] = new byte[0];
		decompresser.reset();
		decompresser.setInput(input);
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];

			int got;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * Compress string
	 * 
	 * @param input
	 *            string source
	 * @return compressed string
	 * @see #decompressString(String)
	 */
	public static String compressString(String input) {
		byte[] result = compressBytes(input.getBytes());
		return Base64.encodeBase64String(result);
	}

	/**
	 * Decompress string
	 * 
	 * @param input
	 *            compressed string source
	 * @return Uncompress string
	 * @see #compressString(String)
	 */
	public static String decompressString(String input) {
		byte[] result = Base64.decodeBase64(input);
		return new String(decompressBytes(result));
	}

	public static void main(String argv[]) throws Exception {
		String str = "select top 100 customer_guid,customername,gender,customerid,tel_1,tel_2,mobile,address from customer where 1=1";

		String result = compressString(str);
		System.out.println(result);
		System.out.println(decompressString(result));
	}
}
