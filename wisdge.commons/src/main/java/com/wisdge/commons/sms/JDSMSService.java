package com.wisdge.commons.sms;

import com.wisdge.dataservice.utils.JSonUtils;
import com.wisdge.dataservice.xhr.XHRPoolService;
import com.wisdge.utils.DateUtils;
import com.wisdge.utils.security.MD5;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class JDSMSService extends AbstractSmsService {
	private String url;
	private String name;
	private String pwd;
	private Map<String, String> templateIds = new HashMap<>();
	@Autowired
	private XHRPoolService xhrService;

	public static JDSMSService getInstance(Map<String, Object> injectMapper) {
		JDSMSService instance = new JDSMSService();
		try {
			BeanUtils.populate(instance, injectMapper);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return instance;
	}

	@Override
	public SmsResponse send(String[] mobiles, Map<String, Object> paramsMap, String smsType) throws Exception {
		String content = templateIds.get(smsType);
		if (StringUtils.isEmpty(content)) {
			log.debug("不能识别的短信模版：{}", smsType);
			return SmsResponse.build();
		}

		String code = (String) paramsMap.get("code");
		content = content.replace("${code}", code);
		String urlString = "{0}?name={1}&pwd={2}&phone={3}&content={4}&mttime={5}&rpttype=1";
		try {
			String mttime = DateUtils.format(new Date(), "yyyyMMddhhmmss");
			String password = MD5.encrypt32(pwd + mttime);
			urlString = MessageFormat.format(urlString, url, name, password, mobiles[0], content, mttime);
			String resultString = xhrService.post(urlString);
			log.debug("urlString: {} resultString: {}", urlString, resultString);
			Map<String, Object> resultObject = JSonUtils.read(resultString, Map.class);
			String reqCode = (String) resultObject.get("ReqCode");
			return SmsResponse.build("00".equals(reqCode) ? 1 : 0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return SmsResponse.build();
		}
	}
}
