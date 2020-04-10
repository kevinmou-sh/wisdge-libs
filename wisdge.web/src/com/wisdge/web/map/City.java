package com.wisdge.web.map;

import org.dom4j.Element;
import com.wisdge.utils.PinyinUtils;

public class City {

	private String name = null;
	private String abbr = null;
	private int zipstart = 0;
	private int zipend = 0;
	private String alies;

	public City(Element element) {
		name = element.attributeValue("Label");
		abbr = element.attributeValue("Abbr");
		zipstart = Integer.parseInt(element.attributeValue("PC-Start"));
		zipend = Integer.parseInt(element.attributeValue("PC-End"));
		alies = PinyinUtils.getPinyinAbbr(name);
	}

	public int[] getZip() {
		return new int[] { zipstart, zipend };
	}

	public int getZipstart() {
		return zipstart;
	}

	public int getZipend() {
		return zipend;
	}

	public String getName() {
		return name;
	}

	public String getAbbr() {
		return abbr;
	}

	public String getAlies() {
		return alies;
	}
}