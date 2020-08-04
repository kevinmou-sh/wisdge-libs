package com.wisdge.dataservice.sql;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class RowMapperAdapter implements IRowMapper {

    @Override
    public void rowMap(Map<String, Object> map) throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field: fields) {
            String fieldName = field.getName().replace("_", "").toUpperCase();
            if (map.containsKey(fieldName))
                field.set(this, map.get(fieldName));
        }
    }
}
