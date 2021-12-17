package com.wisdge.commons.filestorage;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.event.ProgressEvent;
import com.qcloud.cos.event.ProgressEventType;
import com.qcloud.cos.event.ProgressListener;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import com.wisdge.utils.StringUtils;
import com.wisdge.web.springframework.WebUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Data
public class QCOSStorageClient implements IFileStorageClient {
	private String bucketName;
	private String accessKeyId;
	private String accessKeySecret;
	private String endpoint;
	@Setter(AccessLevel.NONE)
	private COSClient cosClient;
	private String wanEndpoint = "";   //OSS外网地址
	private String httpProtocol = "";   //OSS协议
	private String remoteRoot;
	private boolean downloadFromURL;// 是否通过oss的url直接下载
	private long expiredMinutes; // oss的url多少分钟后过期
	private boolean security;
	private String[] ignoreFileTypes = {"ftlh", "fra"};

	public void init(boolean security) {
		log.debug("Tencent COS service initializing remoteRoot： {}", remoteRoot);
		log.debug("bucketName, endpoint: {} wanEndpoint: {} ", bucketName, endpoint, wanEndpoint);
		String secretId = accessKeyId;
		String secretKey = accessKeySecret;
		COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
		Region region = new Region(endpoint);
		ClientConfig clientConfig = new ClientConfig(region);
		if (StringUtils.isNotEmpty(httpProtocol)) {
			clientConfig.setHttpProtocol(HttpProtocol.valueOf(httpProtocol));
		}
		// 3 生成 cos 客户端。
		cosClient = new COSClient(cred, clientConfig);
		if (cosClient.doesBucketExist(bucketName)) {
			log.debug("已创建Bucket：{}", bucketName);
		} else {
			log.debug("Bucket不存在，创建Bucket：{}", bucketName);
			cosClient.createBucket(bucketName);
		}

		this.security = security;
	}

	@Override
	public void destroy() {
		cosClient.shutdown();
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (InputStream is = new ByteArrayInputStream(data)) {
			ObjectMetadata metadata = new ObjectMetadata();
			setFileExtension(metadata, filepath);
			metadata.setContentLength(data.length);
			PutObjectResult putObjectResult = cosClient.putObject(bucketName, filepath, is, metadata);
			if (putObjectResult != null) {
				filepath = filepath.replace(remoteRoot + "/", "");
				return filepath;
			}
		}
		throw new NotImplementedException("上传文件失败");
	}

	private  void setFileExtension(ObjectMetadata metadata, String filepath) throws Exception{
		String fileExtension = FilenameUtils.getExtension(filepath);
		Object[] flag = Arrays.stream(ignoreFileTypes).filter(type -> type.equals(fileExtension)).toArray();
		if (flag.length == 0) {
			metadata.setContentType(WebUtils.getContentType(fileExtension));
		}
	}

	@Override
	public String saveStream(String filepath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (InputStream source = inputStream) {
			ObjectMetadata metadata = new ObjectMetadata();
			setFileExtension(metadata, filepath);
			metadata.setContentLength(size);
//			cosClient.putObject(bucketName, filepath, source, metadata);
			PutObjectResult putObjectResult = cosClient.putObject(new PutObjectRequest(bucketName, filepath, source, metadata).withGeneralProgressListener(new PutObjectProgressListenerWithTC(progressListener)));
			if (putObjectResult != null) {
				filepath = filepath.replace(remoteRoot + "/", "");
				return filepath;
			}
		}
		throw new NotImplementedException("上传文件失败");
	}

	public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
		return saveStream(filepath, inputStream, size, null);
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		try (COSObject cosObject = cosClient.getObject(bucketName, filepath)) {
			// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
			return toByteArray(cosObject.getObjectContent());
		}
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		if (filepath.startsWith("/"))
			filepath = filepath.substring(1);
		FileMetadata metadata = new FileMetadata();
		if (this.isDownloadFromURL()) {
			URL url = cosClient.generatePresignedUrl(bucketName, filepath, getExpiredDate(), HttpMethodName.POST);
			String urlString = url.toString();
			log.debug("urlString: {} wanEndpoint: {}  endpoint: {} ", urlString, wanEndpoint, endpoint);
			urlString = urlString.replace("http:", "").replace("https:", "");
			metadata.setDownloadURL(urlString);
			executor.execute(null, metadata);
		} else {
			try (COSObject ossObject = cosClient.getObject(bucketName, filepath)) {
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
		cosClient.deleteObject(bucketName, filepath);
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
		if (this.expiredMinutes <= 0) {
			this.expiredMinutes = 24 * 60; // 默认1天超时
		}
		return new Date(now + this.expiredMinutes * 60 * 1000L);
	}
}

@Slf4j
class PutObjectProgressListenerWithTC implements ProgressListener {
	private long bytesWritten = 0;
	private long totalBytes = -1;
	private boolean succeed = false;
	private IProgressListener progressListener;

	PutObjectProgressListenerWithTC(IProgressListener iProgressListener) {
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

