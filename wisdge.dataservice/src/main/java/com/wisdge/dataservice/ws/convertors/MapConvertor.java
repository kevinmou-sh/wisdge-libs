package com.wisdge.dataservice.ws.convertors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Map转换器
 * 
 * @author Kevin MOU
 * @version 1.0.1
 */
@XmlType(name = "MapConvertor")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapConvertor {
	private List<MapEntry> entries = new ArrayList<MapEntry>();

	public void addEntry(MapEntry entry) {
		entries.add(entry);
	}

	public static class MapEntry {
		private Object key;
		private Object value;

		public MapEntry() {
			super();
		}

		public MapEntry(Map.Entry<Object, Object> entry) {
			super();
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public MapEntry(Object key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}

		public Object getKey() {
			return key;
		}

		public void setKey(Object key) {
			this.key = key;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	public List<MapEntry> getEntries() {
		return entries;
	}
}
