package com.wisdge.commons.sms;

import lombok.Data;

@Data
public class SmsResponse {
    private int succeed;
    private SmsCaptcha[] caches;

    public static SmsResponse build() {
        return new SmsResponse(0);
    }
    public static SmsResponse build(int count) {
        return new SmsResponse(count);
    }

    public SmsResponse(int count) {
        this.succeed = count;
    }
}


