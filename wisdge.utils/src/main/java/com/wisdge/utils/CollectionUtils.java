package com.wisdge.utils;

import java.util.Collection;

import org.springframework.util.ObjectUtils;

public class CollectionUtils extends org.springframework.util.CollectionUtils {

	public CollectionUtils() {
		super();
	}
	
	public static String join(Collection<?> collection, String split) {
		if (collection == null)
			return "";
		
		return join(collection.toArray(), split);
	}

	
	public static String join(Object[] items, String split) {
		if (items == null)
			return "";
		
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<items.length; i++) {
			if (i > 0)
				builder.append(split);
			builder.append(items[i].toString());
		}
		return builder.toString();
	}
	
	public static boolean contains(Object[] items, Object element) {
		for(int i=0; i<items.length; i++) {
			if (ObjectUtils.nullSafeEquals(items[i], element))
				return true;
		}
		return false;
	}
}
