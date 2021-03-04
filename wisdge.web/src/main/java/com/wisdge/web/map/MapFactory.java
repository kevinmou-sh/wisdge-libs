package com.wisdge.web.map;

import java.util.*;
import java.io.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Description: Web Map Factory Copyright:(c) 2011 Wisdge.com
 * 
 * @author Kevin MOU
 * @version 1.4
 */
public class MapFactory {
	private List<Region> regionList;

	public MapFactory() {
		init();
	}

	private void init() {
		if (regionList != null)
			return;

		regionList = new ArrayList<Region>();
		InputStream in = this.getClass().getResourceAsStream("china_map.xml");
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		// load all of region
		List<? extends Node> list = document.selectNodes("/xMap/Region");
		for (int i = 0; i < list.size(); i++) {
			Node node = (Node) list.get(i);
			Region region = new Region((Element) node);
			regionList.add(region);
		}

		document.clearContent();
	}

	public Region getRegion(String name) {
		for (Region region : regionList) {
			if (region.getName().equals(name))
				return region;
		}

		return null;
	}

	public List<Region> getRegions() {
		return regionList;
	}

	/**
	 * @param name
	 *            Name of province
	 * @return Get province by name
	 */
	public Province getProvince(String name) {
		for (Region region : regionList) {
			Province province = region.getProvinceByName(name);
			if (province != null)
				return province;
		}

		return null;
	}

	/**
	 * @return Get all provinces
	 */
	public List<Province> getProvinces() {
		List<Province> provinces = new ArrayList<Province>();
		for (Region region : regionList) {
			provinces.addAll(region.getProvinces());
		}
		return provinces;
	}

	public City getCity(String name) {
		for (Region region : regionList) {
			for (Province province : region.getProvinces()) {
				City city = province.getCityByName(name);
				if (city != null)
					return city;
			}
		}

		return null;
	}

	public static void main(String[] argv) {
		MapFactory mapFactory = new MapFactory();
		Province province = mapFactory.getProvince("浙江省");
		for (City city : province.getCities()) {
			System.out.println(city.getName());
		}
	}

}
