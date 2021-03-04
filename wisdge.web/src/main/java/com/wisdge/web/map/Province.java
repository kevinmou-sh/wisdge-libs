package com.wisdge.web.map;

import java.util.*;
import org.dom4j.Element;
import org.dom4j.Node;
import com.wisdge.utils.PinyinUtils;

public class Province {

	private String name = null;
	private String abbr = null;
	private List<City> cities = new ArrayList<City>();

	public Province(Element element) {
		name = element.attributeValue("Label");
		abbr = element.attributeValue("Abbr");
		
		List<? extends Node> list = element.selectNodes("City");
		for (Node node: list) {
			City city = new City((Element) node);
			cities.add(city);
		}
	}
	
	public int size() {
		return cities.size();
	}
	
	public String getName() {
		return name;
	}
	
	public String getAbbr() {
		return abbr;
	}
	
	public String getAlias() {
		return PinyinUtils.getPinyinAbbr(name);
	}

	public City getCityById(int idx) {
		if (cities.size() >= idx)
			return cities.get(idx);
		else
			return null;
	}

	public City getCityByName(String name) {
		for (City city: cities) {
			if (city.getName().equals(name))
				return city;
		}
		return null;
	}

	public City getCityByAbbr(String abbr) {
		for (City city: cities) {
			if (city.getAbbr().equals(abbr))
				return city;
		}
		return null;
	}
	
	public List<City> getCities() {
		return cities;
	}

	public City searchCityByAbbr(String alias) {
		for (City city: cities) {
			if (city.getAlies().equals(alias))
				return city;
		}
		return null;
	}
	
	public boolean containCity(String name) {
		return (getCityByName(name) != null);
	}
}