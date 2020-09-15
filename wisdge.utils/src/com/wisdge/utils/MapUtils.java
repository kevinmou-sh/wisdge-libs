package com.wisdge.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static Map<String, Object> make(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
