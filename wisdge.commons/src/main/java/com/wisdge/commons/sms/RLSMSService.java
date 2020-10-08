package com.wisdge.commons.sms;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RLSMSService extends AbstractSmsService {
	private static final Logger logger = LoggerFactory.getLogger(RLSMSService.class);
	public static final String APP_URL = "app.cloopen.com";
	public static final String APP_PORT = "8883";

	private String accId;
	private String accToken;
	private String appId;
	private Map<String, String> templateIds;
	
	public RLSMSService() {
		templateIds = new HashMap<>();
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public String getAccToken() {
		return accToken;
	}

	public void setAccToken(String accToken) {
		this.accToken = accToken;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Map<String, String> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(Map<String, String> templateIds) {
		this.templateIds = templateIds;
	}

	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType) {
		String templateId = templateIds.get(smsType);
		if (StringUtils.isEmpty(templateId)) {
			logger.error("不能识别的短信模版：{}", smsType);
			return 0;
		}

		// 初始化SDK
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init(APP_URL, APP_PORT);
		restAPI.setAccount(accId, accToken);
		restAPI.setAppId(appId);
		
		String[] params = paramsMap.values().toArray(new String[0]);
		int resultCount = 0;
		for (String mobile : mobiles) {
			logger.debug("Send message by YUNTONGXUN with template: {}", templateId);
			HashMap<String, Object> result = restAPI.sendTemplateSMS(mobile, templateId, params);
			String statusCode = (String) result.get("statusCode");
			if (statusCode.equals("000000")) {
				// 正常返回输出data包体信息（map）
				resultCount += 1;
			} else {
				// 异常返回输出错误码和错误信息
				logger.error("[{}] {}", statusCode, result.get("statusMsg"));
			}
		}
		return resultCount;
	}
}
