package com.wisdge.ftp;

import java.io.Serializable;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class FTPConfig implements Serializable {
	private String hostname;
	private int port;
	private String username;
	private String password;
	private boolean passive;
	private boolean ssl;
	private boolean ssh;
	private int timeout;
	private boolean implicit;
	private String protocol;
	private String trustManager;
	private boolean forcePortP;
	private String remoteRoot;

	public FTPConfig() {
		this.ssl = false;
		this.port = 21;
		this.passive = false;
		this.hostname = "localhost";
		this.ssh = false;
		this.timeout = 0;
	}

	public FTPConfig(String hostname, int port, String username, String password) {
		this(false, hostname, port, username, password);
	}

	public FTPConfig(boolean ssl, String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.ssl = ssl;
		this.passive = false;
	}

	public String toString() {
		String string = (ssl ? "[SFTP]" : "[FTP]") + this.username + ":" + this.password + "@" + this.hostname;
		if (port != 21)
			string += ":" + port;
		if (! StringUtils.isEmpty(remoteRoot)) {
			if (remoteRoot.charAt(0) == '/')
				remoteRoot = remoteRoot.substring(1);
			string += "/" + remoteRoot;
		}
		return string;
	}

}
