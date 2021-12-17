package com.wisdge.commons.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsCaptcha {
    private String mobile;
    private String captcha;
}
