package com.wisdge.commons.sms;

import lombok.Data;

@Data
public class SmsResponse {
    private int successCount;
    private String captcha;

    public static SmsResponse build() {
        return new SmsResponse();
    }

    public SmsResponse withSuccess(int successCount) {
        this.setSuccessCount(successCount);
        return this;
    }

    public SmsResponse withCaptcha(String captcha) {
        this.setCaptcha(captcha);
        return this;
    }
}
