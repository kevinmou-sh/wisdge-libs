package com.wisdge.dataservice.rowset;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auto ware POJO bean from ResultRowSet or ResultSignleRow
 * 
 * @author Kevin MOU
 * @version 1.1.0.20130108
 */
public class BeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

	/**
	 * 转换ResultRowSet对象到 List<T>对象
	 * 
	 * @param set
	 *            ResultRowSet对象
	 * @param beanClass
	 *            Class<T>
	 * @return List<T>
	 */
	@SuppressWarnings("restriction")
	public static <T> List<T> mapRowerList(ResultRowSet set, Class<T> beanClass) {
		List<T> list = new ArrayList<T>();
		if (set == null) {
			return list;
		}

		for (int i = 0; i < set.getRowsSize(); i++) {
			T bean = null;
			try {
				bean = beanClass.newInstance();
				Field[] fields = getClassFields(beanClass);
				for (Field field : fields) {
					String methodName = "set" + upperCaseFirst(field.getName());
					Object column = set.getColumn(i, field.getName());
					if (column == null) {
						continue;
					}
					if (column instanceof com.sun.org.apache.xerces.internal.dom.ElementNSImpl) {
						continue;
					}
					setFieldValue(bean, methodName, column, field);
				}
				list.add(bean);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return list;
	}

	/**
	 * 转换ResultSingleRow对象到 T对象
	 * 
	 * @param row
	 *            ResultSingleRow对象
	 * @param beanClass
	 *            Class<T>
	 * @return T
	 */
	@SuppressWarnings("restriction")
	public static <T> T mapRower(ResultSingleRow row, Class<T> beanClass) {
		if (row == null) {
			return null;
		}

		T bean = null;
		try {
			bean = beanClass.newInstance();
			Field[] fields = getClassFields(beanClass);
			for (Field field : fields) {
				String methodName = "set" + upperCaseFirst(field.getName());
				Object column = row.getColumn(field.getName());
				if (column == null) {
					continue;
				}
				if (column instanceof com.sun.org.apache.xerces.internal.dom.ElementNSImpl) {
					continue;
				}
				setFieldValue(bean, methodName, column, field);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return bean;
	}

	public static Date xgc2Date(XMLGregorianCalendar xgc) {
		return xgc.toGregorianCalendar().getTime();
	}

	/**
	 * 字符串首字符大写
	 * 
	 * @param s
	 * @return String
	 */
	public static String upperCaseFirst(String s) {
		if (s == null || s.length() < 1) {
			return s;
		}

		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * 获得Class<T>的所有定义字段，包括父类和接口类
	 * 
	 * @param clazz
	 *            Class<T>
	 * @return Field[]
	 */
	public static Field[] getClassFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		collectionAddAll(fields, clazz.getDeclaredFields());
		if (clazz.getSuperclass() != null) {
			collectionAddAll(fields, getClassFields(clazz.getSuperclass()));
		}

		fields.addAll(getInterfacesFields(clazz));
		return fields.toArray(new Field[0]);
	}

	private static List<Field> getInterfacesFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			collectionAddAll(fields, getClassFields(interfaceClass));
		}
		return fields;
	}

	private static void collectionAddAll(List<Field> list, Field[] fields) {
		for (Field field : fields) {
			list.add(field);
		}
	}

	private static void setFieldValue(Object bean, String methodName, Object value, Field field) {
		Method method = null;
		try {
			method = bean.getClass().getMethod(methodName, field.getType());
			if (value instanceof XMLGregorianCalendar) {
				value = xgc2Date((XMLGregorianCalendar) value);
			}
			method.invoke(bean, value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
