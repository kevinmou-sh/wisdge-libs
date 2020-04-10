package com.wisdge.ftp;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class FTPConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	private String hostname;
	private int port;
	private String username;
	private String password;
	private boolean passive;
	private boolean ssl;
	private boolean ssh;
	private int timeout;
	private boolean implicit;
	private String protocal;
	private String trustmgr;
	private boolean forcePortP;
	private String remoteRoot;

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isSsh() {
		return ssh;
	}

	public void setSsh(boolean ssh) {
		this.ssh = ssh;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public String getTrustmgr() {
		return trustmgr;
	}

	public void setTrustmgr(String trustmgr) {
		this.trustmgr = trustmgr;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	public boolean isForcePortP() {
		return forcePortP;
	}

	public void setForcePortP(boolean forcePortP) {
		this.forcePortP = forcePortP;
	}

	public String getRemoteRoot() {
		return remoteRoot;
	}

	public void setRemoteRoot(String remoteRoot) {
		this.remoteRoot = remoteRoot;
	}

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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
