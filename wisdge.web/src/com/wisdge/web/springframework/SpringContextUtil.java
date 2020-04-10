package com.wisdge.web.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static Log log = LogFactory.getLog(SpringContextUtil.class);
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
		log.debug(SpringContextUtil.applicationContext);
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz, String beanName) {
		Object object = getBean(beanName);
		if (object != null)
			return (T) object;
		return null;
	}

	public static Object getBean(String beanName) {
		if (applicationContext == null) {
			log.error("SpringFramework没有获得有效的上下文对象。");
			return null;
		}
		
		if (applicationContext.containsBean(beanName)) {
			return applicationContext.getBean(beanName);
		} else {
			log.error((new StringBuilder("Cann't find bean (")).append(beanName).append(") from application context.").toString());
			return null;
		}
	}
	
	public static boolean containsBean(String beanName) {
		return applicationContext.containsBean(beanName);
	}
	
	public static boolean hasBean(String beanName) {
		return applicationContext.containsBean(beanName);
	}
	
	/**
     * 根据类名获取到bean
     * @param <T>
     * @param clazz
     * @return
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByName(Class<T> clazz) throws BeansException {
        char[] cs=clazz.getSimpleName().toCharArray();
        cs[0] += 32;//首字母大写到小写
        //System.out.println(ctx.getBean(String.valueOf(cs)));
        String beanName = String.valueOf(cs);
		return (T) getBean(beanName);
    }
    
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }
}
