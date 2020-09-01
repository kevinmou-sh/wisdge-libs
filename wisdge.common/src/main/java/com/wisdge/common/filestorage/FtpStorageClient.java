package com.wisdge.common.filestorage;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.wisdge.ftp.FTPConfig;
import com.wisdge.ftp.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;

public class FtpStorageClient extends FTPConfig implements IFileStorageClient {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(FtpStorageClient.class);
	private String remoteRoot;

	@Override
	public void init() {

	}

	@Override
	public String getRemoteRoot() {
		return remoteRoot;
	}
	
	public void setRemoteRoot(String remoteRoot) {
		this.remoteRoot = remoteRoot;
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		FtpUtils.storeFile(this, filepath, data);
		return "";
	}

	@Override
	public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
		FtpUtils.storeStream(this, filepath, inputStream);
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
			ChannelSftp sftp = FtpUtils.getChannel(this);
			SftpATTRS attrs = sftp.lstat(filepath);
			try (InputStream is = FtpUtils.retrieveStream(sftp, filepath)) {
				if (attrs != null) {
					metadata.setContentLength(attrs.getSize());
				} else {
					logger.debug("lstat file {} failed, cannot get file size", filepath);
				}
				executor.execute(is, metadata);
			} finally {
				FtpUtils.closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = FtpUtils.getClient(this);
			FTPFile file = ftpClient.mlistFile(filepath);
			try (InputStream is = FtpUtils.retrieveStream(ftpClient, filepath)) {
				if (file != null) {
					metadata.setContentLength(file.getSize());
				} else {
					logger.debug("List file {} failed, cannot get file size", filepath);
				}
				executor.execute(is, metadata);
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
