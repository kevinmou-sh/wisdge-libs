package com.wisdge.commons.es;

import lombok.Data;

@Data
public class ESServer {
	private String host = "127.0.0.1";
    private int port = 9200;
    private String schema = "http";
}
