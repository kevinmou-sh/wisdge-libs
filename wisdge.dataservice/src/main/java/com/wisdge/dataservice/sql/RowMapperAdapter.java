package com.wisdge.dataservice.sql;

import lombok.extern.slf4j.Slf4j;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public abstract class RowMapperAdapter implements IRowMapper {

    @Override
    public void rowMap(Map<String, Object> map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
            List<PropertyDescriptor> propertyDescriptors = Arrays.asList(beanInfo.getPropertyDescriptors());
            // log.info("PropertyDescriptors count is {}", propertyDescriptors.size());
            Iterator<String> iter = map.keySet().iterator();
            while(iter.hasNext()) {
                String columnName = iter.next();
                // log.info("Find column in map: {}", columnName);
                for(PropertyDescriptor property: propertyDescriptors) {
                    // log.info("Match field {} vs {}", property.getName(), columnName);
                    if (property.getName().equalsIgnoreCase(columnName)) {
                        Method setter = property.getWriteMethod();
                        setter.invoke(this, map.get(columnName));
                        break;
                    }
                    if (property.getName().equalsIgnoreCase(columnName.replace("_", ""))) {
                        Method setter = property.getWriteMethod();
                        setter.invoke(this, map.get(columnName));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
