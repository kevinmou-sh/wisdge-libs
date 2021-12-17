package com.wisdge.commons.sms;

import com.wisdge.dataservice.xhr.XHRPoolService;
import com.wisdge.web.springframework.SpringContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class CupDataSmsService extends AbstractSmsService {
    private String uri;

    @Override
    public SmsResponse send(String[] mobiles, Map<String, Object> paramsMap, String smsType) throws Exception {
        XHRPoolService xhrPoolService = SpringContextUtil.getBean("xhrPoolService");
        if (xhrPoolService == null)
            throw new NullPointerException("XHRPoolService has not defined");

        int resultCount = 0;
        List<SmsCaptcha> caches = new ArrayList<>();
        for(String mobile: mobiles) {
            try {
                JSONObject requestJSON = new JSONObject();
                requestJSON.put("mobile", mobiles[0]);
                log.debug("Request: {}", requestJSON.toString());
                String resp = xhrPoolService.post(this.uri, requestJSON.toString());
//            String resp = " {\n" +
//                    "                \"msg\": \"\",\n" +
//                    "                \"code\": \"0000\",\n" +
//                    "                \"data\": {\n" +
//                    "                    \"smsCode\": \"187526\"\n" +
//                    "                }\n" +
//                    "            }";
                log.debug("Response: {}", resp);
                JSONObject responseJSON = new JSONObject(resp);
                if ("0000".equals(responseJSON.get("code"))) {
                    JSONObject dataJSON = responseJSON.optJSONObject("data");
                    if (dataJSON != null) {
                        resultCount ++;
                        caches.add(new SmsCaptcha(mobile, dataJSON.optString("smsCode")));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        SmsResponse smsResponse = SmsResponse.build(resultCount);
        smsResponse.setCaches(caches.toArray(new SmsCaptcha[0]));
        return smsResponse;
    }
}
