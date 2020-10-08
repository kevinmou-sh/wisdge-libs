package com.wisdge.commons.sms;

import java.util.Date;
import java.util.Map;

public interface ISmsService {
	/**
	 * 发送短信
	 * @param mobiles String[] 接收短信的手机号码
	 * @param paramsMap  短信入参
	 * @param smsType String 短信类型
	 * @param sendTime Date 短信发送的预定时间，默认为null
	 * @param sign String 签名
	 * @return int 成功发送的条数
	 */
	int send(String[] mobiles, Map<String, Object> paramsMap, String smsType, Date sendTime, String sign);
	int send(String[] mobiles, Map<String, Object> paramsMap, String smsType);

	/**
	* @Title: 发送短信
	* @param mobiles  接收短信的手机号码
	* @param paramsMap  短信入参
	* @return
	* String  返回的随机码
	* @throws
	*/
	String send(String[] mobiles, Map<String, Object> paramsMap);
}
