package com.wisdge.dataservice.xhr;

import java.io.Serializable;

public class FileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String filename;
	private byte[] data;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public FileBean(String filename, byte[] data) {
		this.filename = filename;
		this.data = data;
	}

}
