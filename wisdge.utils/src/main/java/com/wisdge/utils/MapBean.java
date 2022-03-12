package com.wisdge.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Map bean, includes key and value.
 * 
 * @author Kevin MOU
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapBean implements Serializable {
	private Object key;
	private Object value;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
