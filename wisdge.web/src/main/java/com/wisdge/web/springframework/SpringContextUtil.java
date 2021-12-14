package com.wisdge.web.springframework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
		log.debug("Initialized springframework context");
	}

	public static void setAppCtx(ApplicationContext applicationContext) {
		if (SpringContextUtil.applicationContext == null) {
			SpringContextUtil.applicationContext = applicationContext;
			log.debug("Initialized springframework context");
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static boolean containsBean(String beanName) {
		return getApplicationContext().containsBean(beanName);
	}

	public static <T> T getBean(String beanName) {
		if (getApplicationContext().containsBean(beanName)) {
			return (T) getApplicationContext().getBean(beanName);
		}
		return null;
	}

	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return getApplicationContext().getBean(beanName, clazz);
	}

	public static <T> T getBean(Class<T> clazz, String beanName) {
		return getApplicationContext().getBean(beanName, clazz);
	}

	public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(beanName);
    }
}
