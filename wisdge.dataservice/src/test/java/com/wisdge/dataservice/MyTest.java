package com.wisdge.dataservice;

import com.wisdge.dataservice.sql.SqlFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

public class MyTest {
    @Test
    public void test() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(SqlFactory.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            List<PropertyDescriptor> descriptors = Arrays.asList(propertyDescriptors);
            System.out.println("PropertyDescriptors " + descriptors.size());
            for(PropertyDescriptor property : descriptors) {
                System.out.println(property.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
