package com.wisdge.commons.filestorage;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import com.wisdge.ftp.FTPConfig;
import com.wisdge.ftp.FtpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.InputStream;

@Slf4j
@Data
public class FtpStorageClient extends FTPConfig implements IFileStorageClient {
	private String remoteRoot;
	private boolean security;

	@Override
	public void init(boolean security) {
		this.security = security;
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		FtpUtils.storeFile(this, filepath, data);
		return "";
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
		return saveStream(filePath, inputStream, size);
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		FtpUtils.storeStream(this, filePath, inputStream);
		return "";
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		return FtpUtils.retrieveFile(this, filepath);
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		FileMetadata metadata = new FileMetadata();
		if (this.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = FtpUtils.getChannel(this);
				SftpATTRS attrs = sftp.lstat(filepath);
				try (InputStream is = FtpUtils.retrieveStream(sftp, filepath)) {
					if (attrs != null) {
						metadata.setContentLength(attrs.getSize());
					} else {
						log.debug("lstat file {} failed, cannot get file size", filepath);
					}
					executor.execute(is, metadata);
				}
			} finally {
				FtpUtils.closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = FtpUtils.getClient(this);
			try {
				FTPFile file = ftpClient.mlistFile(filepath);
				try (InputStream is = FtpUtils.retrieveStream(ftpClient, filepath)) {
					if (file != null) {
						metadata.setContentLength(file.getSize());
					} else {
						log.debug("List file {} failed, cannot get file size", filepath);
					}
					executor.execute(is, metadata);
				}
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	@Override
	public void delete(String filepath) throws Exception {
		FtpUtils.deleteFile(this, filepath);
	}

	@Override
	public void destroy() {

	}

}
