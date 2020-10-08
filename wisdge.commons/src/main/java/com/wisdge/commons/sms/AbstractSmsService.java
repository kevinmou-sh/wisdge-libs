package com.wisdge.commons.sms;

import java.util.Date;
import java.util.Map;

public abstract class AbstractSmsService implements ISmsService {

	@Override
	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType) {
		return this.send(mobiles, paramsMap, smsType, null, null);
	}

	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType, Date sendDate) {
		return this.send(mobiles, paramsMap, smsType, sendDate, null);
	}

	@Override
	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType, Date sendTime, String sign) {
		return 0;
	}

	@Override
	public String send(String[] mobiles, Map<String, Object> paramsMap) {
		return null;
	}

}
