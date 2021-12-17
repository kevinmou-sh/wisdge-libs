package com.wisdge.commons.filestorage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Data
public class JDOSSStorageClient implements IFileStorageClient {
	private String oosDomain;
	private String region;
	private String bucketName;
	private String accessKey;
	private String secretSecret;
	@Setter(AccessLevel.NONE)
	private AmazonS3 oosClient;
	private boolean security;
	private String remoteRoot = "";

	public void init(boolean security) {
		log.debug("JD OSS service initializing remoteRoot： {}", remoteRoot);
		log.debug("Region: {}, BucketName: {} ", region, bucketName);

		ClientConfiguration config = new ClientConfiguration();
		AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(oosDomain, this.region);
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretSecret);
		AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
		oosClient = AmazonS3Client.builder()
				.withEndpointConfiguration(endpointConfig)
				.withClientConfiguration(config)
				.withCredentials(awsCredentialsProvider)
				.disableChunkedEncoding()
				.withPathStyleAccessEnabled(true)
				.build();
		this.security = security;
	}

	@Override
	public String save(String filePath, byte[] data) throws Exception {
		try (InputStream is = new ByteArrayInputStream(data)) {
			if (filePath.startsWith("/"))
				filePath = filePath.substring(1);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(is.available());
			PutObjectResult objectResult = oosClient.putObject(this.bucketName, filePath, is, meta);
			if (objectResult != null) {
				filePath = filePath.replace(remoteRoot + "/", "");
				return filePath;
			} else throw new NotImplementedException("上传文件失败");
		}
	}

	public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
		return saveStream(filepath, inputStream, size, null);
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		try (InputStream source = inputStream){
			if (filePath.startsWith("/"))
				filePath = filePath.substring(1);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(size);
			PutObjectResult objectResult = oosClient.putObject(this.bucketName, filePath, source, meta);
			if (objectResult != null) {
				filePath = filePath.replace(remoteRoot + "/", "");
				return filePath;
			} else throw new NotImplementedException("上传文件失败");
		}
	}

	@Override
	public byte[] retrieve(String filePath) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (S3Object object = oosClient.getObject(new GetObjectRequest(this.bucketName, filePath));){
			// 这里ossObject.getObjectContent()的输入流，在OSSObject的close就会关闭了，不需要再次关闭
			try(InputStream is = object.getObjectContent()) {
				return toByteArray(is);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.elite.commons.filestorage.IFileStorageClient#retrieveStream(java.lang.String, com.elite.commons.filestorage.IFileExecutor)
	 */
	@Override
	public void retrieveStream(String filePath, IFileExecutor executor) throws Exception {
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		try (S3Object ossObject = oosClient.getObject(new GetObjectRequest(this.bucketName, filePath));) {
			FileMetadata metadata = new FileMetadata();
			ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
			metadata.setContentLength(objectMetadata.getContentLength());
			metadata.setLastModified(objectMetadata.getLastModified().getTime());
			try (InputStream is = ossObject.getObjectContent()) {
				executor.execute(is, metadata);
			}
		}
	}

	private byte[] toByteArray(InputStream in) throws Exception {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024 * 4];
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}
			byte[] data = out.toByteArray();
			return data;
		}
	}

	@Override
	public void delete(String fileKey) throws Exception {
		oosClient.deleteBucket(fileKey);
		try {
			AmazonS3 oos = new AmazonS3Client(new AWSCredentials() {
				public String getAWSAccessKeyId() {
					return accessKey;
				}

				public String getAWSSecretKey() {
					return secretSecret;
				}
			});
			oos.setEndpoint(this.oosDomain);
			oos.deleteObject(bucketName, fileKey);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void destroy() {
		oosClient.shutdown();
	}
}
