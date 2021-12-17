package com.wisdge.commons.sms;

import java.util.Map;

public interface ISmsService {
	/**
	 * 发送短信
	 * @param mobiles String[] 接收短信的手机号码
	 * @param paramsMap  短信入参
	 * @param smsType String 短信类型
	 * @return int 成功发送的条数
	 */
	SmsResponse send(String[] mobiles, Map<String, Object> paramsMap, String smsType) throws Exception;

	SmsResponse send(String mobiles, Map<String, Object> paramsMap, String smsType) throws Exception;
}
