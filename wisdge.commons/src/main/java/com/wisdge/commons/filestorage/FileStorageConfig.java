package com.wisdge.commons.filestorage;

import lombok.Data;

@Data
public class FileStorageConfig {
    private String type;
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private boolean downloadFromUrl;
    private int expiredMinutes;
    private String wanEndpoint;
    private String remoteRoot;
    private boolean security;

    private String host;
    private String protocol;
    private String username;
    private String password;
    private int port = 21;
    private boolean ssh;
    private boolean ssl;
    private boolean forcePort;
    private boolean implicit;
    private boolean passive;

    private int connectTimeout;
    private int networkTimeout;

    private boolean httpAntiStealToken;
    private String httpSecretKey;
    private int httpTrackerHttpPort;
    private String httpTrackerServers;
    private int poolSize = 5;

    private String saveUrl;
    private String retrieveUrl;
    private String deleteUrl;
    private String inputField = "file";
    private String pathField = "path";

    private boolean https;
    private String region;
    private int indexAtomic;

    private String oosDomain;

    private String ignoreFileTypes;
}
