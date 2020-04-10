package com.wisdge.utils;

import java.io.Serializable;

/**
 * Map bean, includes key and value.
 * 
 * @author Kevin MOU
 */
public class MapBean implements Serializable {
	private static final long serialVersionUID = 1640861108270730474L;
	private Object key;
	private Object value;

	public MapBean() {

	}

	public MapBean(Object key, Object value) {
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

	@Override
	public String toString() {
		return "MapBean [key=" + key + ", value=" + value + "]";
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
