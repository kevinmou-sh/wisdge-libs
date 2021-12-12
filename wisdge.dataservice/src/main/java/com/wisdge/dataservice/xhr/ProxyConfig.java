package com.wisdge.dataservice.xhr;

import lombok.Data;

@Data
public class ProxyConfig {
	private String host;
	private int port;
	private String username;
	private String password;
}
