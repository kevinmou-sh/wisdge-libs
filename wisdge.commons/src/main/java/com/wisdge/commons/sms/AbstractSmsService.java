package com.wisdge.commons.sms;

import java.util.Map;

public abstract class AbstractSmsService implements ISmsService {

	@Override
	public SmsResponse send(String[] mobiles, Map<String, Object> paramsMap, String smsType) throws Exception {
		return SmsResponse.build();
	}

	@Override
	public SmsResponse send(String mobile, Map<String, Object> paramsMap, String smsType) throws Exception {
		return send(new String[]{ mobile }, paramsMap, smsType);
	}

}
