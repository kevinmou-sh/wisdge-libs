package com.wisdge.dataservice.xhr;

import java.io.Serializable;
import com.wisdge.utils.StringUtils;

public class ProxyConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;
	private String username;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean avaliable() {
		return !StringUtils.isEmpty(host);
	}
	
	public ProxyConfig() {
		
	}

	public ProxyConfig(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public ProxyConfig(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
}
