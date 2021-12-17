package com.wisdge.commons.sms;

import lombok.Data;
import java.io.Serializable;

@Data
public class CaptchaCache implements Serializable {
	private String captcha;
	private String mobile;
	private boolean verify = false;
}
