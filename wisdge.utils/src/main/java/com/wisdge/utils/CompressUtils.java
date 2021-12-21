package com.wisdge.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
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
	private static final int cachesize = 1024;
	private static final Inflater decompresser = new Inflater();
	private static final Deflater compresser = new Deflater();

	/**
	 * Compress from byte[]
	 * 
	 * @param input
	 *            uncompress bytes source
	 * @return compressed bytes
	 * @see #decompressBytes(byte[])
	 */
	public static byte[] compressBytes(byte input[]) throws IOException {
		compresser.reset();
		compresser.setInput(input);
		compresser.finish();
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			byte[] buf = new byte[cachesize];
			while (!compresser.finished()) {
				byteArrayOutputStream.write(buf, 0, compresser.deflate(buf));
			}
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * Decompress from bytes
	 * 
	 * @param input
	 *            compressed source bytes
	 * @return Uncompress bytes
	 * @see #compressBytes(byte[])
	 */
	public static byte[] decompressBytes(byte input[]) throws IOException, DataFormatException {
		decompresser.reset();
		decompresser.setInput(input);
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			byte[] buf = new byte[cachesize];
			while (!decompresser.finished()) {
				byteArrayOutputStream.write(buf, 0, decompresser.inflate(buf));
			}
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * Compress string
	 * 
	 * @param input
	 *            string source
	 * @return compressed string
	 * @see #decompressString(String)
	 */
	public static String compressString(String input) throws IOException {
		return Base64.encodeBase64String(compressBytes(input.getBytes()));
	}

	/**
	 * Decompress string
	 * 
	 * @param input
	 *            compressed string source
	 * @return Uncompress string
	 * @see #compressString(String)
	 */
	public static String decompressString(String input) throws DataFormatException, IOException {
		return new String(decompressBytes(Base64.decodeBase64(input)));
	}
}
