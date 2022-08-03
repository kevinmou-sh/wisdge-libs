package com.wisdge.commons.filestorage;

import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.InputStream;

@Slf4j
@Data
public class QVodStorage implements IFileStorageClient {

    private boolean security;
    private VodUploadClient client;
    private String secretId;
    private String secretKey;
    private String subAppId;
    private Integer concurrentUploadNumber = 0;
    private String region;

    @Override
    public void init(boolean security) {
        this.security = security;
        client = new VodUploadClient(secretId, secretKey);
        log.debug("-----QCloud Vod Initialization Success-----");
    }

    @Override
    public String getRemoteRoot() {
        return null;
    }

    @Override
    public String save(String filepath, byte[] data) throws Exception {
        init(security);
        VodUploadRequest request = new VodUploadRequest();
        String[] file1 = filepath.split(",");
        String mediaFilePath = "";
        String coverFilePath = "";
        if (filepath.contains(",")) {
            mediaFilePath = file1[0];
            coverFilePath = file1[1];
        } else {
            mediaFilePath = filepath;
        }
        File file = new File(mediaFilePath);
        //设置分区视频最小为800MB
        long max = 800 * 1024 * 1024;
        log.debug("file name: {}, file length: {}", file.getName(), file.length());
        //媒体文件本地路径，不支持 URL

        request.setMediaFilePath(mediaFilePath);
        if (StringUtils.isNotEmpty(coverFilePath)) {
            request.setCoverFilePath(coverFilePath);
        }
        request.setSubAppId(Long.valueOf(subAppId));
        if (file.length() >= max && concurrentUploadNumber > 0) {
            request.setConcurrentUploadNumber(concurrentUploadNumber);
        }
        String fileId = "";
        try {
            VodUploadResponse response = client.upload(region, request);
            fileId = response.getFileId();
            log.info("Upload FileId :{}", fileId);
            log.info("Upload RequestId :{}", response.getRequestId());
        } catch (Exception e) {
            // 业务方进行异常处理
            log.error("Upload Err", e);
        }
        return fileId;
    }

    @Override
    public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
        return null;
    }

    @Override
    public String saveStream(String filepath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
        return saveStream(filepath, inputStream, size);
    }

    @Override
    public byte[] retrieve(String filepath) throws Exception {
        return new byte[0];
    }

    @Override
    public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {

    }

    @Override
    public void delete(String filepath) throws Exception {

    }

    @Override
    public boolean isSecurity() {
        return false;
    }

    @Override
    public void destroy() {

    }

}
