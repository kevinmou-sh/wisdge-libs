package com.wisdge.common.filestorage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class AliOSSStorageClient implements IFileStorageClient {
	private static final Logger logger = LoggerFactory.getLogger(AliOSSStorageClient.class);
	private String bucketName;
	private String accessKeyId;
	private String accessKeySecret;
	private String endpoint;
	private OSS ossClient;
	private String remoteRoot;
	private boolean downloadFromURL;// 是否通过oss的url直接下载
	private long expiredMins; // oss的url多少分钟后过期

	public boolean isDownloadFromURL() {
		return downloadFromURL;
	}

	public void setDownloadFromURL(boolean downloadFromURL) {
		this.downloadFromURL = downloadFromURL;
	}

	public long getExpiredMins() {
		return expiredMins;
	}

	public void setExpiredMins(long expiredMins) {
		this.expiredMins = expiredMins;
	}

	@Override
	public String getRemoteRoot() {
		return remoteRoot;
	}

	public void setRemoteRoot(String remoteRoot) {
		this.remoteRoot = remoteRoot;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void init() {
		logger.debug("Aliyun OSS service initializing remoteRoot： {}", remoteRoot);
		ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
		if (ossClient.doesBucketExist(bucketName)) {
			logger.debug("已创建Bucket：{}", bucketName);
		} else {
			logger.debug("Bucket不存在，创建Bucket：{}", bucketName);
			ossClient.createBucket(bucketName);
		}
		try {
			BucketInfo info = ossClient.getBucketInfo(bucketName);
			logger.debug("Bucket {} {}", bucketName, "的信息如下：");
			logger.debug("\t数据中心：{}", info.getBucket().getLocation());
			logger.debug("\t创建时间：{}", info.getBucket().getCreationDate());
			logger.debug("\t用户标志：{}", info.getBucket().getOwner());
		} catch (Exception e) {
			logger.info("保险经纪不兼容getBucketInfo这个方法，他们的云服务器是阿里云的阉割版");
		}
	}

	@Override
	public void destroy() {
		ossClient.shutdown();
	}
	
	@Override
	public String save(String filepath, byte[] data) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (InputStream is = new ByteArrayInputStream(data)) {
			ossClient.putObject(bucketName, filepath, is);
			return "";
		}
	}

	@Override
	public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (InputStream source = inputStream) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(size);
			ossClient.putObject(bucketName, filepath, source, metadata);
		}
		return "";
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (OSSObject ossObject = ossClient.getObject(bucketName, filepath)) {
			// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
			return toByteArray(ossObject.getObjectContent());
		}
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		FileMetadata metadata = new FileMetadata();
		if (this.isDownloadFromURL()) {
			URL url = ossClient.generatePresignedUrl(bucketName, filepath, getExpiredDate());
			metadata.setDownloadURL(url.toString());
			executor.execute(null, metadata);
		} else {
			try (OSSObject ossObject = ossClient.getObject(bucketName, filepath)) {
				ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
				metadata.setContentLength(objectMetadata.getContentLength());
				metadata.setLastModified(objectMetadata.getLastModified().getTime());
				// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
				executor.execute(ossObject.getObjectContent(), metadata);
			}
		}
	}

	@Override
	public void delete(String filepath) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		ossClient.deleteObject(bucketName, filepath);
	}

	private byte[] toByteArray(InputStream in) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024 * 4];
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}
			return out.toByteArray();
		}
	}

	private Date getExpiredDate() {
		long now = System.currentTimeMillis();
		if (this.getExpiredMins() <= 0) {
			this.setExpiredMins(24 * 60);// 默认1天超时
		}
		return new Date(now + this.getExpiredMins() * 60 * 1000L);
	}
}
