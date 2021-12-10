package com.wisdge.dataservice.xhr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class XhrConfig {
    private int maxConnection = 300;
    private int maxPerRoute = 100;
    private int maxRetryCount = 3;
    private int requestTimeout = 600000;
    private int connectTimeout = 60000;
    private int socketTimeout = 600000;
    private String protocol = "TLSv1.2";
    private ProxyConfig proxyConfig;
}
