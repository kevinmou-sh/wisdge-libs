package com.wisdge.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import com.jcraft.jzlib.DeflaterOutputStream;
import com.jcraft.jzlib.InflaterInputStream;

/**
 * 压缩方法类
 * 
 * @author Kevin MOU
 * @version 1.0
 */
public final class ZipUtils {
	/**
	 * 解压缩ZIP文件压缩包
	 * 
	 * @param zipFile
	 *            ZIP文件的FILE对象
	 * @return 解压后的文件或者目录的FILE对象
	 * @throws IOException
	 *             IO异常
	 */
	public static File expandZipFile(File zipFile) throws IOException {
		String parent = zipFile.getParent();
		String dirName = zipFile.getName().split(".zip")[0];
		File directory = new File(parent, dirName);
		expandZipFile(zipFile, directory);
		return directory;
	}

	/**
	 * 解压缩ZIP文件压缩包中的特定目标文件
	 * 
	 * @param zipFile
	 *            ZIP文件的FILE对象
	 * @param target
	 *            解压目标文件
	 * @throws IOException
	 *             IO异常
	 */
	public static void expandZipFile(File zipFile, File target) throws IOException {
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry;
		while ((entry = zipIn.getNextEntry()) != null) {
			outputFileFromStream(zipIn, entry.getName(), target);
		}
		zipIn.close();
	}

	/**
	 * 从一个输入流对象中读取文件存放到指定的文件目录
	 * 
	 * @param in
	 *            输入流对象
	 * @param name
	 *            目标文件
	 * @param parent
	 *            目标文件的父结点
	 * @throws IOException
	 */
	private static void outputFileFromStream(InputStream in, String name, File parent) throws IOException {
		File outFile = new File(parent, name);
		(new File(outFile.getParent())).mkdirs();
		outFile.createNewFile();
		FileOutputStream out = new FileOutputStream(outFile);
		byte buf[] = new byte[0x20000];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		out.close();
	}
	
	private static final int BUFFER = 8192;

	public static void compress(String srcPath , String dstPath) throws IOException{
	    File srcFile = new File(srcPath);
	    File dstFile = new File(dstPath);
	    if (! srcFile.exists()) {
	        throw new FileNotFoundException(srcPath + "不存在！");
	    }

	    FileOutputStream out = new FileOutputStream(dstFile);
        CheckedOutputStream cos = new CheckedOutputStream(out, new CRC32());
        ZipOutputStream zipOut = new ZipOutputStream(cos);
	    try {
	        compress(srcFile, zipOut, "");
	    } finally {
	    	zipOut.close();
	    	cos.close();
	    	out.close();
	    }
	}

	public static byte[] compress(String srcPath) throws IOException{
	    File srcFile = new File(srcPath);
	    if (! srcFile.exists()) {
	        throw new FileNotFoundException(srcPath + "不存在！");
	    }
	    return compress(new File(srcPath));
	}

	public static byte[] compress(File srcFile) throws IOException{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CheckedOutputStream cos = new CheckedOutputStream(baos, new CRC32());
        ZipOutputStream zipOut = new ZipOutputStream(cos);
	    try {
	        compress(srcFile, zipOut, "");
	        return baos.toByteArray();
	    } finally {
            zipOut.close();
            cos.close();
            baos.close();
	    }
	}

	public static byte[] compress(File[] srcFiles) throws IOException{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CheckedOutputStream cos = new CheckedOutputStream(baos, new CRC32());
        ZipOutputStream zipOut = new ZipOutputStream(cos);
	    try {
	        for(File file: srcFiles)
	        	compress(file, zipOut, "");
	        return baos.toByteArray();
	    } finally {
            zipOut.close();
            cos.close();
            baos.close();
	    }
	}

	public static void compress(File[] srcFiles, File dstFile) throws IOException{
		FileOutputStream out = new FileOutputStream(dstFile);
        CheckedOutputStream cos = new CheckedOutputStream(out, new CRC32());
        ZipOutputStream zipOut = new ZipOutputStream(cos);
	    try {
	        for(File file: srcFiles)
	        	compress(file, zipOut, "");
	    } finally {
            zipOut.close();
            cos.close();
	    	out.close();
	    }
	}

	private static void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException{
	    if (file.isDirectory()) {
	        compressDirectory(file, zipOut, baseDir);
	    } else {
	        compressFile(file, zipOut, baseDir);
	    }
	}

	/** 压缩一个目录 */
	private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException{
	    File[] files = dir.listFiles();
	    for (int i = 0; i < files.length; i++) {
	        compress(files[i], zipOut, baseDir + dir.getName() + "/");
	    }
	}

	/** 压缩一个文件 */
	private static void compressFile(File file, ZipOutputStream zipOut, String baseDir) throws IOException{
	    if (!file.exists()){
	        return;
	    }

	    BufferedInputStream bis = null;
	    try {
	        bis = new BufferedInputStream(new FileInputStream(file));
	        ZipEntry entry = new ZipEntry(baseDir + file.getName());
	        entry.setSize(bis.available());
	        zipOut.putNextEntry(entry);
	        int count;
	        byte data[] = new byte[BUFFER];
	        while ((count = bis.read(data, 0, BUFFER)) != -1) {
	            zipOut.write(data, 0, count);
	        }
	        zipOut.closeEntry();
	    } finally {
	        if(null != bis){
	            bis.close();
	        }
	    }
	}

	/**
	 * 使用那GZip压缩
	 * 
	 * @param data
	 *            被压缩的的字符数组
	 * @return 压缩完成后的字符数组
	 */
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * 对GZip压缩包进行解压
	 * 
	 * @param data
	 *            已压缩的的字符数组
	 * @return 解压缩完成后的字符数组
	 */
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * 进行ZIP压缩
	 * 
	 * @param data
	 *            被压缩的的字符数组
	 * @return 压缩完成后的字符数组
	 */
	public static byte[] zip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(bos);
			ZipEntry entry = new ZipEntry("zip");
			entry.setSize(data.length);
			zip.putNextEntry(entry);
			zip.write(data);
			zip.closeEntry();
			zip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * 对ZIP压缩包进行解压
	 * 
	 * @param data
	 *            已压缩的的字符数组
	 * @return 解压缩完成后的字符数组
	 */
	public static byte[] unZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ZipInputStream zip = new ZipInputStream(bis);
			while (zip.getNextEntry() != null) {
				byte[] buf = new byte[1024];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = zip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
			}
			zip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * 字节数组转成hex字符串
	 * 
	 * @param bArray
	 *            字符数组
	 * @return hex字符串
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);

		for (byte element : bArray) {
			String sTemp = Integer.toHexString(0xFF & element);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 进行JZLib压缩
	 * 
	 * @param data
	 *            被压缩的的字符数组
	 * @return 压缩完成后的字符数组
	 */
	public static byte[] jzlib(byte[] data) {
		byte[] object = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DeflaterOutputStream zOut = new DeflaterOutputStream(out);
			DataOutputStream objOut = new DataOutputStream(zOut);
			objOut.write(data);
			objOut.flush();
			zOut.close();
			object = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 对JZLib包进行解压
	 * 
	 * @param data
	 *            已压缩的的字符数组
	 * @return 解压缩完成后的字符数组
	 */
	public static byte[] unjzlib(byte[] data) {
		byte[] result = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			InflaterInputStream zIn = new InflaterInputStream(in);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = zIn.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			result = baos.toByteArray();
			baos.flush();
			baos.close();
			zIn.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Test
	public void test() throws IOException {
		ZipUtils.compress("/Users/kevinmou/Documents/temp", "/Users/kevinmou/Documents/temp/test.zip");
	}
}
