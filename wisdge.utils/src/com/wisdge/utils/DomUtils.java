package com.wisdge.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Description: Dom4j extend class
 * 
 * @author Kevin MOU
 * last modified: 2020/01/30
 */
public class DomUtils {
	/**
	 * 从一个URL 中读入Document
	 * 
	 * @param url
	 *            指定的URL
	 * @return Document对象
	 * @throws IOException, DocumentException 
	 */
	public static Document parser(URL url) throws IOException, DocumentException {
		java.io.InputStream in = url.openStream();
		return parser(in);
	}

	/**
	 * 从一个InputStream 中读入Document
	 * 
	 * @param in
	 *            被指定的InputStream
	 * @return Document对象
	 * @throws IOException, DocumentException 
	 */
	public static Document parser(java.io.InputStream in) throws IOException, DocumentException {
		SAXReader sr = new SAXReader();
		Document document = sr.read(in);
		return document;
	}

	/**
	 * 从一个字符串中读入Document
	 * 
	 * @param strXML
	 *            包含DOM的字符串
	 * @return Document对象
	 * @throws IOException, DocumentException 
	 */
	public static Document parser(String strXML) throws IOException, DocumentException {
		return parser(strXML, StandardCharsets.UTF_8);
	}

	/**
	 * 从一个字符串中读入Document
	 * 
	 * @param strXML
	 *            包含DOM的字符串
	 * @param charset
	 *            XML的字符集
	 * @return Document对象
	 * @throws IOException
	 */
	public static Document parser(String strXML, Charset charset) throws IOException, DocumentException {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(strXML.getBytes(charset))) {
			Document document = parser(inputStream);
			inputStream.close();
			return document;
		} catch(Exception e) {
			throw e;
		}
	}

	/**
	 * 从一个字符串中读入Document
	 * 
	 * @param strXML
	 *            包含DOM的字符串
	 * @param charsetName
	 *            XML string的字符集名称
	 * @return Document对象
	 * @throws IOException, DocumentException
	 */
	public static Document parser(String strXML, String charsetName) throws IOException, DocumentException {
		return parser(strXML, Charset.forName(charsetName));
	}

	/**
	 * 将Document输出到字符串中
	 * 
	 * @param document
	 *            被写出的Document对象
	 * @return 按照格式化输出的字符串
	 * @see #prettyPrint(Element)
	 */
	public static String prettyPrint(Document document) {
		try(StringWriter out = new StringWriter()) {
			write(document, out);
			return out.getBuffer().toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 将Element输出到字符串中
	 * 
	 * @param element
	 *            被写出的Element对象
	 * @return 按照格式化输出的字符串
	 * @see #prettyPrint(Document)
	 */
	public static String prettyPrint(Element element) {
		try(StringWriter out = new StringWriter()) {
			write(element, out);
			return out.getBuffer().toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 对Document对象在指定的out上进行写出
	 * 
	 * @see #write(Document, Writer)
	 * @param document
	 *            被写出的Document对象
	 * @param os
	 *            被指定输出的OutputStream
	 * @throws IOException
	 *             IO异常
	 */
	public static void write(Document document, OutputStream os) throws IOException {
		// lets write to a file
		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(os, format);
		xmlWriter.write(document);
		xmlWriter.close();
	}
	
	/**
	 * 对Document对象在指定的out上进行写出
	 * 
	 * @see #write(Document, Writer)
	 * @param element
	 *            被写出的Element对象
	 * @param os
	 *            被指定输出的OutputStream
	 * @throws IOException
	 *             IO异常
	 */
	public static void write(Element element, OutputStream os) throws IOException {
		// lets write to a file
		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(os, format);
		xmlWriter.write(element);
		xmlWriter.close();
	}

	/**
	 * 对Document对象在指定的out上进行写出
	 * 
	 * @see #write(Document, OutputStream)
	 * @param document
	 *            被写出的Document对象
	 * @param writer
	 *            被指定输出的Writer
	 * @throws IOException
	 *             IO异常
	 */
	public static void write(Document document, Writer writer) throws IOException {
		// lets write to a file
		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		xmlWriter.write(document);
		xmlWriter.close();
	}

	/**
	 * 对Document对象在指定的out上进行写出
	 * 
	 * @see #write(Document, OutputStream)
	 * @param element
	 *            被写出的Element对象
	 * @param writer
	 *            被指定输出的Writer
	 * @throws IOException
	 *             IO异常
	 */
	public static void write(Element element, Writer writer) throws IOException {
		// lets write to a file
		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		xmlWriter.write(element);
		xmlWriter.close();
	}
}
