package com.wisdge.web.map;

import java.util.*;
import org.dom4j.Element;
import org.dom4j.Node;

public class Region {

	private List<Province> provinces = new ArrayList<Province>();
	private String name = null;
	private String abbr = null;
	private String alias = null;

	public Region(Element element) {
		name = element.attributeValue("Label");
		abbr = element.attributeValue("Abbr");
		alias = element.attributeValue("Alies");
		List<? extends Node> list = element.selectNodes("Province");
		for(Node el: list) {
			Province province = new Province((Element)el);
			provinces.add(province);
		}
	}

	public int size() {
		return provinces.size();
	}

	public String getName() {
		return name;
	}

	public String getAbbr() {
		return abbr;
	}

	public String getAlias() {
		return alias;
	}

	public Province getProvinceById(int idx) {
		if (provinces.size() >= idx)
			return provinces.get(idx);
		else
			return null;
	}

	public Province getProvinceByName(String name) {
		for(Province province: provinces) {
			if (province.getName().equals(name))
				return province;
		}
		return null;
	}

	public Province getProvinceByAbbr(String abbr) {
		for(Province province: provinces) {
			if (province.getAbbr().equals(abbr))
				return province;
		}
		return null;
	}

	public Province getProvinceByAlies(String alias) {
		for(Province province: provinces) {
			if (province.getAlias().equals(alias))
				return province;
		}
		return null;
	}
	
	public List<Province> getProvinces() {
		return provinces;
	}
	
	public boolean containProvince(String name) {
		return (getProvinceByName(name) != null);
	}
}