package com.wisdge.commons.filestorage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import com.wisdge.utils.StringUtils;
import com.wisdge.web.springframework.WebUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
@Data
public class AliOSSStorageClient implements IFileStorageClient {
	private String bucketName;
	private String accessKeyId;
	private String accessKeySecret;
	private String endpoint;
	@Setter(AccessLevel.NONE)
	private OSS ossClient;
	private String remoteRoot;
	private boolean downloadFromURL;	// 是否通过OSS的URL直接下载
	private String wanEndpoint;   		// OSS外网地址
	private long expiredMinutes; 		// OSS的URL在多少分钟后过期
	private boolean security;

	@Override
	public void init(boolean security) {
		this.security = security;

		log.debug("Aliyun OSS service initializing remoteRoot： {}", remoteRoot);
		log.debug("Endpoint: {}, WanEndpoint: {} ", endpoint, wanEndpoint);
		ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
		if (ossClient.doesBucketExist(bucketName)) {
			log.debug("已创建Bucket：{}", bucketName);
		} else {
			log.debug("Bucket不存在，创建Bucket：{}", bucketName);
			ossClient.createBucket(bucketName);
		}
		try {
			BucketInfo info = ossClient.getBucketInfo(bucketName);
			log.debug("Bucket {} {}", bucketName, "的信息如下：");
			log.debug("\t数据中心：{}", info.getBucket().getLocation());
			log.debug("\t创建时间：{}", info.getBucket().getCreationDate());
			log.debug("\t用户标志：{}", info.getBucket().getOwner());
		} catch (Exception e) {
			log.warn("BucketInfo信息获取失败");
		}
	}

	@Override
	public String save(String filePath, byte[] data) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (InputStream is = new ByteArrayInputStream(data)) {
			ossClient.putObject(bucketName, filePath, is);
			return "";
		}
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (InputStream source = inputStream) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(WebUtils.getContentType(FilenameUtils.getExtension(filePath)));
			metadata.setContentLength(size);
			ossClient.putObject(bucketName, filePath, source, metadata);
		}
		return "";
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (InputStream source = inputStream) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(size);
			ossClient.putObject(new PutObjectRequest(bucketName, filePath, source, metadata).withProgressListener(new PutObjectProgressListener(progressListener)));
		}
		return "";
	}

	@Override
	public byte[] retrieve(String filePath) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (OSSObject ossObject = ossClient.getObject(bucketName, filePath)) {
			// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
			return toByteArray(ossObject.getObjectContent());
		}
	}

	@Override
	public void retrieveStream(String filePath, IFileExecutor executor) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		FileMetadata metadata = new FileMetadata();
		if (this.isDownloadFromURL()) {
			URL url = ossClient.generatePresignedUrl(bucketName, filePath, getExpiredDate());
			String urlString = url.toString();
			if (StringUtils.isNotEmpty(wanEndpoint)) {
				String internalEndpoint = endpoint.substring(endpoint.indexOf("//") + 2);
				urlString = urlString.replace(internalEndpoint, wanEndpoint);
			} else {
				urlString = urlString.replace("http:", "").replace("https:", "");
			}
			log.debug("UrlString:{}, Endpoint:{}, WanEndpoint:{}", urlString, endpoint, wanEndpoint);
			metadata.setDownloadURL(urlString);
			executor.execute(null, metadata);
		} else {
			try (OSSObject ossObject = ossClient.getObject(bucketName, filePath)) {
				ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
				metadata.setContentLength(objectMetadata.getContentLength());
				metadata.setContentType(objectMetadata.getContentType());
				metadata.setLastModified(objectMetadata.getLastModified().getTime());
				// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
				executor.execute(ossObject.getObjectContent(), metadata);
			}
		}
	}

	@Override
	public void delete(String filePath) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		ossClient.deleteObject(bucketName, filePath);
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
		if (this.getExpiredMinutes() <= 0) {
			this.setExpiredMinutes(24 * 60);// 默认1天超时
		}
		return new Date(now + this.getExpiredMinutes() * 60 * 1000L);
	}

	@Override
	public void destroy() {
		ossClient.shutdown();
	}
}

@Slf4j
class PutObjectProgressListener implements ProgressListener {
	private long bytesWritten = 0;
	private long totalBytes = -1;
	private boolean succeed = false;
	private IProgressListener progressListener;

	PutObjectProgressListener(IProgressListener iProgressListener) {
		this.progressListener = iProgressListener;
	}

	@Override
	public void progressChanged(ProgressEvent progressEvent) {
		long bytes = progressEvent.getBytes();
		ProgressEventType eventType = progressEvent.getEventType();
		switch (eventType) {
			case TRANSFER_STARTED_EVENT:
				log.trace("Start to upload......");
				break;
			case REQUEST_CONTENT_LENGTH_EVENT:
				this.totalBytes = bytes;
				log.trace(this.totalBytes + " bytes in total will be uploaded to OSS");
				break;
			case REQUEST_BYTE_TRANSFER_EVENT:
				this.bytesWritten += bytes;
				if (this.totalBytes != -1) {
					if (this.progressListener != null) {
						this.progressListener.progressChanged(this.bytesWritten, this.totalBytes);
					}
					int percent = (int)(this.bytesWritten * 100.0 / this.totalBytes);
					log.trace(bytes + " bytes have been written at this time, upload progress: " + percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")");
				} else {
					log.trace(bytes + " bytes have been written at this time, upload ratio: unknown" + "(" + this.bytesWritten + "/...)");
				}
				break;
			case TRANSFER_COMPLETED_EVENT:
				this.succeed = true;
				log.trace("Succeed to upload, " + this.bytesWritten + " bytes have been transferred in total");
				break;
			case TRANSFER_FAILED_EVENT:
				log.trace("Failed to upload, " + this.bytesWritten + " bytes have been transferred");
				break;
			default:
				break;
		}
	}

	public boolean isSucceed() {
		return succeed;
	}
}
