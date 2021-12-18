package com.wisdge.commons.filestorage;

import com.wisdge.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
@Slf4j
public class FileServiceConfigurer {
    private Map<String, FileStorageConfig> storages;
    /**
     * 禁止的文件类型，多个类型以逗号分隔
     */
    private String forbidden;
    /**
     * 接受的文件类型，多个类型以逗号分隔
     */
    private String accept;
    /**
     * 使用301进行转向的过期时间（秒），仅定义参数，不具体实现
     */
    private long expires;

    public FileStorage getFileStorage() {
        FileStorage fileStorage = new FileStorage(StringUtils.toSet(forbidden, ","), StringUtils.toSet(accept, ","));
        storages.forEach((key, config) -> {
            String type = config.getType();
            if ("AliOSS".equalsIgnoreCase(type)) {
                AliOSSStorageClient fileStorageClient = new AliOSSStorageClient();
                fileStorageClient.setEndpoint(config.getEndpoint());
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKeyId(config.getAccessKeyId());
                fileStorageClient.setAccessKeySecret(config.getAccessKeySecret());
                fileStorageClient.setDownloadFromURL(config.isDownloadFromUrl());
                fileStorageClient.setExpiredMinutes(config.getExpiredMinutes());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.setWanEndpoint(config.getWanEndpoint());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("ftp".equalsIgnoreCase(type)) {
                FtpStorageClient fileStorageClient = new FtpStorageClient();
                fileStorageClient.setProtocol(config.getProtocol());
                fileStorageClient.setHostname(config.getHost());
                fileStorageClient.setUsername(config.getUsername());
                fileStorageClient.setPassword(config.getPassword());
                fileStorageClient.setSsl(config.isSsl());
                fileStorageClient.setSsh(config.isSsh());
                fileStorageClient.setPassive(config.isPassive());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.setForcePortP(config.isForcePort());
                fileStorageClient.setImplicit(config.isImplicit());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("fastDFS".equalsIgnoreCase(type)) {
                FastDFSStorageClient fileStorageClient = new FastDFSStorageClient();
                fileStorageClient.setCharset(StandardCharsets.UTF_8.name());
                fileStorageClient.setConnectTimeout(config.getConnectTimeout());
                fileStorageClient.setNetworkTimeout(config.getNetworkTimeout());
                fileStorageClient.setHttpAntiStealToken(config.isHttpAntiStealToken());
                fileStorageClient.setHttpSecretKey(config.getHttpSecretKey());
                fileStorageClient.setHttpTrackerHttpPort(config.getHttpTrackerHttpPort());
                fileStorageClient.setHttpTrackerServers(config.getHttpTrackerServers());
                fileStorageClient.setPoolSize(config.getPoolSize());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("http".equalsIgnoreCase(type)) {
                HttpStorageClient fileStorageClient = new HttpStorageClient();
                fileStorageClient.setInputField(config.getInputField());
                fileStorageClient.setPathField(config.getPathField());
                fileStorageClient.setDeleteUrl(config.getDeleteUrl());
                fileStorageClient.setRetrieveUrl(config.getRetrieveUrl());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("minio".equalsIgnoreCase(type)) {
                MinioStorageClient fileStorageClient = new MinioStorageClient();
                fileStorageClient.setEndpoint(config.getEndpoint());
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKey(config.getAccessKeyId());
                fileStorageClient.setAccessSecret(config.getAccessKeySecret());
                fileStorageClient.setHttps(config.isHttps());
                fileStorageClient.setRegion(config.getRegion());
                fileStorageClient.setIndex(config.getIndexAtomic());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("amazon".equalsIgnoreCase(type)) {
                AmazonOSSStorage fileStorageClient = new AmazonOSSStorage();
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKey(config.getAccessKeyId());
                fileStorageClient.setSecretSecret(config.getAccessKeySecret());
                fileStorageClient.setRegion(config.getRegion());
                fileStorageClient.setOosDomain(config.getOosDomain());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("JDOss".equalsIgnoreCase(type)) {
                JDOSSStorageClient fileStorageClient = new JDOSSStorageClient();
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKey(config.getAccessKeyId());
                fileStorageClient.setSecretSecret(config.getAccessKeySecret());
                fileStorageClient.setRegion(config.getRegion());
                fileStorageClient.setOosDomain(config.getOosDomain());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("QOss".equalsIgnoreCase(type)) {
                QCOSStorageClient fileStorageClient = new QCOSStorageClient();
                fileStorageClient.setEndpoint(config.getEndpoint());
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKeyId(config.getAccessKeyId());
                fileStorageClient.setAccessKeySecret(config.getAccessKeySecret());
                fileStorageClient.setDownloadFromURL(config.isDownloadFromUrl());
                fileStorageClient.setExpiredMinutes(config.getExpiredMinutes());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.setWanEndpoint(config.getWanEndpoint());
                fileStorageClient.init(config.isSecurity());
                fileStorageClient.setIgnoreFileTypes(StringUtils.toArray(config.getIgnoreFileTypes(), ","));
                fileStorage.addFileStorage(key, fileStorageClient);
            } else if ("local".equalsIgnoreCase(type)) {
                LocalStorageClient fileStorageClient = new LocalStorageClient();
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(key, fileStorageClient);
            } else {
                log.error("No file storage type match {}", type);
            }
        });
        return fileStorage;
    }
}
