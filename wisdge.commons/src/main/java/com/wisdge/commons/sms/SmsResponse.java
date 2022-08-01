package com.wisdge.commons.sms;

import lombok.Data;

@Data
public class SmsResponse {
    private int succeed;
    private SmsCaptcha[] caches;
    private String code;
    private String message;

    public static SmsResponse build() {
        return new SmsResponse(0, "", "");
    }

    public static SmsResponse build(String code, String message) {
        return new SmsResponse(0, code, message);
    }

    public static SmsResponse build(int count) {
        return new SmsResponse(count, "", "");
    }

    public static SmsResponse build(int count, String code, String message) {
        return new SmsResponse(count, code, message);
    }

    public SmsResponse(int count, String code, String message) {
        this.succeed = count;
        this.code = code;
        this.message = message;
    }
}


