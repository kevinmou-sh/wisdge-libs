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
	private String appUrl = "app.cloopen.com";
	private int appPort = 8883;
	private String accId;
	private String accToken;
	private String appId;
	private Map<String, String> templateIds = new HashMap<>();

	@Override
	public SmsResponse send(String[] mobiles, Map<String, Object> paramsMap, String smsType) throws Exception {
		String templateId = templateIds.get(smsType);
		if (StringUtils.isEmpty(templateId)) {
			throw new NullPointerException("不能识别的短信模版：" +  smsType);
		}

		// 初始化SDK
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init(appUrl, appPort + "");
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
		return SmsResponse.build(resultCount);
	}
}
