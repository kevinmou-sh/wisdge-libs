package com.wisdge.commons.security;

import com.wisdge.dataservice.xhr.XHRPoolService;
import com.wisdge.utils.StringUtils;
import com.wisdge.utils.security.MD5;
import com.wisdge.utils.security.SHA;
import com.wisdge.utils.security.sm.SM3Util;
import org.apache.http.HttpException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SignatureFactory {
	public static final String TYPE_POST = "post";
	public static final String TYPE_GET = "get";
	private String key;
	private String encType;

	public SignatureFactory() {
		encType = "SHA";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getEncType() {
		return encType;
	}

	public void setEncType(String encType) {
		this.encType = encType;
	}

	public String service(String url, Map<String, Object> params, Map<String, String> heads, XHRPoolService service, String httpType) throws Exception {
		Map<String, Object> map = new TreeMap<>(params);

		StringBuilder builder = new StringBuilder();
		for (String key : map.keySet()) {
			builder.append(key).append(map.get(key).toString());
		}

		String sign = "";
		if ("SHA".equalsIgnoreCase(encType))
			sign = (StringUtils.isEmpty(key) ? SHA.encrypt(builder.toString()) : SHA.hmac(builder.toString(), key)).toUpperCase();
		else if ("SM3".equalsIgnoreCase(encType))
			sign = (StringUtils.isEmpty(key) ? SM3Util.hash(builder.toString()) : SM3Util.hmac(builder.toString(), key)).toUpperCase();
		else
			sign = (StringUtils.isEmpty(key) ? MD5.encrypt(builder.toString()) : MD5.hmac(builder.toString(), key)).toUpperCase();
		params.put("sign", sign);

		if (httpType.equalsIgnoreCase(TYPE_GET))
			return service.get(url, params, heads);
		else if (httpType.equalsIgnoreCase(TYPE_POST))
			return service.post(url, params, heads);
		else
			throw new HttpException("不能识别的http服务请求类型：" + httpType);
	}

	public String post(String url, String payload, Map<String, String> headers, XHRPoolService service) throws Exception {
		String sign = "";
		if ("SHA".equalsIgnoreCase(encType))
			sign = (StringUtils.isEmpty(key) ? SHA.encrypt(payload) : SHA.hmac(payload, key)).toUpperCase();
		else if ("SM3".equalsIgnoreCase(encType))
			sign = (StringUtils.isEmpty(key) ? SM3Util.hash(payload) : SM3Util.hmac(payload, key)).toUpperCase();
		else
			sign = (StringUtils.isEmpty(key) ? MD5.encrypt(payload) : MD5.hmac(payload, key)).toUpperCase();
		headers.put("sign", sign);
		return service.post(url, payload, headers);
	}

}
