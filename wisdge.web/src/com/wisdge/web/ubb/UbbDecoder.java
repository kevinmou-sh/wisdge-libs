package com.wisdge.web.ubb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class UbbDecoder {
	private static Log logger = LogFactory.getLog(UbbDecoder.class);
	private static UbbDecoder instance;
	private List<Map<String, String>> decodeItems;
	
	public synchronized static UbbDecoder getInstance(String regexPath) {
		instance = new UbbDecoder(regexPath);
		return instance; 
	}
	
	public synchronized static UbbDecoder getDefaultInstance() {
		if (instance == null)
			instance = new UbbDecoder();
		
		return instance;
	}
	
	public UbbDecoder() {
		this("UbbRegexItems.xml");
	}
	
	public List<Map<String, String>> getItems() {
		return this.decodeItems;
	}
	
	public UbbDecoder(String regexPath) {
		if (decodeItems == null)
			decodeItems = new ArrayList<Map<String, String>>();
		else
			decodeItems.clear();
			
		SAXReader reader = new SAXReader();
		try {
			InputStream in = UbbDecoder.class.getResourceAsStream(regexPath); 
			Document document = reader.read(in);
			in.close();

			Element root = document.getRootElement();
			String lastModified = root.attributeValue("lastModified");
			if (lastModified == null) {
				lastModified = "Naver updated.";
			}
			
			Element regexBlock = (Element)root.selectSingleNode("regexitems");
			loadRegexItems(regexBlock, true);
			Element replaceBlock = (Element)root.selectSingleNode("replaceitems");
			loadRegexItems(replaceBlock, false);
			logger.info("Load xml configuration file[" + regexPath + "] success, Last update:"+lastModified);
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadRegexItems(Element block, boolean flag) {
		if (block != null) {
			List<? extends Node> items = block.selectNodes("item");
			for(int i=0; i<items.size(); i++) {
				Node regexNode = items.get(i).selectSingleNode("regex");
				Node replaceNode = items.get(i).selectSingleNode("replacement");
				if (regexNode== null || replaceNode == null)
					continue;
				Map<String, String> item = new HashMap<String, String>();
				item.put("key", ((Element)regexNode).getTextTrim());
				item.put("value", ((Element)replaceNode).getTextTrim());
				item.put("regex", String.valueOf(flag));
				decodeItems.add(item);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Regex Items:\n");
		for(final Map<String, String> item: decodeItems) {
			if (item.get("regex").equals("true")) {
				buf.append("\tRegex: ").append(item.get("key")).append("\n");
				buf.append("\tValue: ").append(item.get("value")).append("\n");
			} else {
				buf.append("\tReplace: ").append(item.get("key")).append("\n");
				buf.append("\tValue: ").append(item.get("value")).append("\n");
			}
		}
		return buf.toString();
	}

	public String replace(String content) {
		for(final Map<String, String> item: decodeItems) {
			String key = item.get("key");
			String value = item.get("value");
			if (item.get("regex").equals("true")) {
				content = content.replaceAll(key, value);
			} else {
				content = content.replace(key, value);
			}
		}
		
		return content;
	}
	
	public static void main(String[] argv) throws Exception {
		String content= "[img]http://text.com/images[/img]\n"
					  + "[fieldset=3]\n"
					  + "[url]http://a[/url]\n"
					  + "[u]"
					  + "[url=http://b]link[/url]\n"
					  + "[email]kevin.mou@hotmail.com[/email]\n"
					  + "[email=kevmou@gmail.com]Kevin MOU[/email]"+"[/u]\n"
					  + "[/fieldset]";
		System.out.println(UbbDecoder.getDefaultInstance().replace(content));
		
	}
}
