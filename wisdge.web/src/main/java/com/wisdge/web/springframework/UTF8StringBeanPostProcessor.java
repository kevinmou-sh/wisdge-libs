package com.wisdge.web.springframework;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

public class UTF8StringBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof StringHttpMessageConverter) {
			MediaType mediaType = new MediaType("text", "plain", Charset.forName("UTF-8"));
			List<MediaType> types = new ArrayList<MediaType>();
			types.add(mediaType);
			MediaType mediaType2 = new MediaType("application", "json", Charset.forName("UTF-8"));
			types.add(mediaType2);
			MediaType mediaType3 = new MediaType("text", "xml", Charset.forName("UTF-8"));
			types.add(mediaType3);
			
			((StringHttpMessageConverter) bean).setSupportedMediaTypes(types);
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}