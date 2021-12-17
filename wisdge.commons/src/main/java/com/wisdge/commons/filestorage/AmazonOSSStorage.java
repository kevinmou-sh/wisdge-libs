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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Data
public class AmazonOSSStorage implements IFileStorageClient {
    public String oosDomain;
    public String region;
    public String bucketName;
    public String accessKey;
    public String secretSecret;
    public AmazonS3 oos;
    public boolean security;
    public String remoteRoot;

    public void init(boolean security) {
        this.security = security;

        log.debug("init AmazonS3Client oos  oosDomain:{} accessKey: {}  secretSecret: {} bucketName:{} region:{}", this.oosDomain, this.accessKey, this.secretSecret, bucketName, region);
        ClientConfiguration config = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(oosDomain, this.region);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretSecret);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        oos = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfig)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Override
    public String save(String filePath, byte[] data) throws Exception {
        log.debug("filePath: {} oosDomain: {}", filePath, this.oosDomain);
        try(InputStream is = new ByteArrayInputStream(data)) {
            if (filePath.startsWith("/"))
                filePath = filePath.substring(1);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(is.available());
            log.debug("上传文件大小：{}", is.available());
            PutObjectResult objectResult = oos.putObject(this.bucketName, filePath, is, meta);
            if (objectResult != null)
                return filePath;
        } catch (Exception e) {
            log.error("save.e", e);
        }
        throw new NotImplementedException("上传文件失败");
    }

    @Override
    public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
        return saveStream(filePath, inputStream, size);
    }

    @Override
    public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
        try (InputStream source = inputStream) {
            if (filePath.startsWith("/"))
                filePath = filePath.substring(1);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(size);
            PutObjectResult objectResult = oos.putObject(this.bucketName, filePath, source, meta);
            if (objectResult != null)
                return filePath;
        }
        return "";
    }

    @Override
    public byte[] retrieve(String filePath) throws Exception {
        log.error("retrieve filePath: {}", filePath);
        if (filePath.startsWith("/"))
            filePath = filePath.substring(1);
        try (S3Object object = oos.getObject(new GetObjectRequest(this.bucketName, filePath));){
            log.debug("getRedirectLocation: {}", object.getRedirectLocation());
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
        log.error("retrieveStream filePath: {}", filePath);
        if (filePath.startsWith("/"))
            filePath = filePath.substring(1);
        try (S3Object ossObject = oos.getObject(new GetObjectRequest(this.bucketName, filePath));) {
            FileMetadata metadata = new FileMetadata();
            ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
            metadata.setContentLength(objectMetadata.getContentLength());
            metadata.setLastModified(objectMetadata.getLastModified().getTime());
            log.debug("metadata： {}", metadata);
            try (InputStream is = ossObject.getObjectContent()) {
                log.debug("into retrieverStream");
                executor.execute(is, metadata);
            }
        }
    }

    private byte[] toByteArray(InputStream in) throws Exception {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
        try {
            oos.setEndpoint(this.oosDomain);
            oos.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            log.error("remove.e", e);
        }
    }

    @Override
    public void destroy() {

    }

}
