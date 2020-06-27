package com.wisdge.common.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.wisdge.dataservice.utils.JSonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AliSMSService extends AbstractSmsService {
	private static final Logger logger = LoggerFactory.getLogger(AliSMSService.class);
	private static final String LOCATION = "cn-hangzhou";
	private String accessId;
	private String accessSecret;
	private String signName;
	private Map<String, String> templateIds;
	
	public AliSMSService() {
		templateIds = new HashMap<>();
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getSignName() {
		return signName;
	}

	public void setSignName(String signName) {
		this.signName = signName;
	}

	public Map<String, String> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(Map<String, String> templateIds) {
		this.templateIds = templateIds;
	}

	public int send(String[] mobiles, Map<String, Object> paramsMap, String smsType, Date sendTime, String sign) {
		String templateId = templateIds.get(smsType);
		if (StringUtils.isEmpty(templateId)) {
			logger.error("不能识别的短信模版：{}", smsType);
			return 0;
		}

		try {
			// 设置超时时间-可自行调整
			System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
			System.setProperty("sun.net.client.defaultReadTimeout", "10000");
			// 初始化ascClient需要的几个参数
			final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
			final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
			// 初始化ascClient,暂时不支持多region（请勿修改）
			IClientProfile profile = DefaultProfile.getProfile(LOCATION, accessId, accessSecret);
			DefaultProfile.addEndpoint(LOCATION, LOCATION, product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			// 组装请求对象
			SendSmsRequest request = new SendSmsRequest();
			// 使用post提交
			request.setMethod(MethodType.POST);
			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			request.setPhoneNumbers(com.wisdge.utils.CollectionUtils.join(mobiles, ","));
			// 必填:短信签名-可在短信控制台中找到
			request.setSignName(sign != null ? sign : signName);
			// 必填:短信模板-可在短信控制台中找到
			request.setTemplateCode(templateId);
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			request.setTemplateParam(JSonUtils.parse(paramsMap));
			// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
			/** request.setSmsUpExtendCode("90997"); **/
			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
			request.setOutId(smsType);
			// 请求失败这里会抛ClientException异常
			logger.debug("Send message by Aliyun with template: {}", templateId);
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			String returnCode = sendSmsResponse.getCode();
			if ("OK".equals(returnCode)) {
				// 请求成功
				return mobiles.length;
			} else {
				logger.debug("AliSMSService failed: {}", returnCode);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}
}