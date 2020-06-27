package com.wisdge.common.sms;

import java.io.Serializable;

public class CaptchaCache implements Serializable {
	private static final long serialVersionUID = 1L;
	private String captcha;
	private String mobile;
	private boolean verify = false;

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}

}
