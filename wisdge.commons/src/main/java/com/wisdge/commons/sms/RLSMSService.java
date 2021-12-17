package com.wisdge.commons.sms;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class RLSMSService extends AbstractSmsService {
	public static final String APP_URL = "app.cloopen.com";
	public static final String APP_PORT = "8883";

	private String accId;
	private String accToken;
	private String appId;
	private Map<String, String> templateIds = new HashMap<>();

	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType) {
		String templateId = templateIds.get(smsType);
		if (StringUtils.isEmpty(templateId)) {
			log.error("不能识别的短信模版：{}", smsType);
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
			log.debug("Send message by RL with template: {}", templateId);
			HashMap<String, Object> result = restAPI.sendTemplateSMS(mobile, templateId, params);
			String statusCode = (String) result.get("statusCode");
			if (statusCode.equals("000000")) {
				// 正常返回输出data包体信息（map）
				resultCount += 1;
			} else {
				// 异常返回输出错误码和错误信息
				log.error("[{}] {}", statusCode, result.get("statusMsg"));
			}
		}
		return resultCount;
	}
}
