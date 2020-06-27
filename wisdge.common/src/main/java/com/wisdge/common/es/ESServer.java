package com.wisdge.common.es;

public class ESServer {
	private String host = "127.0.0.1";
    private int port = 9200;
    private String schema = "http";
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
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
