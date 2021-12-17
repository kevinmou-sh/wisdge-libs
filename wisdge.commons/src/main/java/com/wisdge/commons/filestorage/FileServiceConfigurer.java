package com.wisdge.commons.filestorage;

import com.wisdge.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;

@Data
@Slf4j
public class FileServiceConfigurer {
    private FileStorageConfig[] storages;
    private String forbidden;
    private String accept;
    private long expires;

    public FileStorage getFileStorage() {
        FileStorage fileStorage = new FileStorage(StringUtils.toSet(forbidden, ","), StringUtils.toSet(accept, ","));
        for(FileStorageConfig config : storages) {
            log.debug(config.toString());
            String name = config.getName();
            String type = config.getType();
            if (type.equalsIgnoreCase("AliOSS")) {
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
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("ftp")) {
                FtpStorageClient fileStorageClient = new FtpStorageClient();
                fileStorageClient.setProtocal(config.getProtocol());
                fileStorageClient.setHostname(config.getHost());
                fileStorageClient.setUsername(config.getUsername());
                fileStorageClient.setPassword(config.getPassword());
                fileStorageClient.setPassive(config.isPassive());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.setForcePortP(config.isForcePort());
                fileStorageClient.setImplicit(config.isImplicit());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("fastDFS")) {
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
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("http")) {
                HttpStorageClient fileStorageClient = new HttpStorageClient();
                fileStorageClient.setInputField(config.getInputField());
                fileStorageClient.setPathField(config.getPathField());
                fileStorageClient.setDeleteUrl(config.getDeleteUrl());
                fileStorageClient.setRetrieveUrl(config.getRetrieveUrl());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("minio")) {
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
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("amazon")) {
                AmazonOSSStorage fileStorageClient = new AmazonOSSStorage();
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKey(config.getAccessKeyId());
                fileStorageClient.setSecretSecret(config.getAccessKeySecret());
                fileStorageClient.setRegion(config.getRegion());
                fileStorageClient.setOosDomain(config.getOosDomain());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("JDOss")) {
                JDOSSStorageClient fileStorageClient = new JDOSSStorageClient();
                fileStorageClient.setBucketName(config.getBucketName());
                fileStorageClient.setAccessKey(config.getAccessKeyId());
                fileStorageClient.setSecretSecret(config.getAccessKeySecret());
                fileStorageClient.setRegion(config.getRegion());
                fileStorageClient.setOosDomain(config.getOosDomain());
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("QOss")) {
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
                fileStorage.addFileStorage(name, fileStorageClient);
            } else if (type.equalsIgnoreCase("local")) {
                LocalStorageClient fileStorageClient = new LocalStorageClient();
                fileStorageClient.setRemoteRoot(config.getRemoteRoot());
                fileStorageClient.init(config.isSecurity());
                fileStorage.addFileStorage(name, fileStorageClient);
            }
        }
        return fileStorage;
    }
}
