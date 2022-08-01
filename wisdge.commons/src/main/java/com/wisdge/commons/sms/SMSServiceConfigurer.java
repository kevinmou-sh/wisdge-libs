package com.wisdge.commons.sms;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Data
@Slf4j
public class SMSServiceConfigurer {
    private String provider;
    private Map<String, Object> properties;

    public ISmsService getService() {
        log.debug("SMS Service provider: {}", provider);
        if ("AliSMSService".equalsIgnoreCase(provider))
            return AliSMSService.getInstance(properties);
        else if ("RLSMSService".equalsIgnoreCase(provider))
            return RLSMSService.getInstance(properties);
        else if ("JDSMSService".equalsIgnoreCase(provider))
            return JDSMSService.getInstance(properties);
        else if ("CupDataSmsService".equalsIgnoreCase(provider))
            return CupDataSmsService.getInstance(properties);
        else return null;
    }
}
