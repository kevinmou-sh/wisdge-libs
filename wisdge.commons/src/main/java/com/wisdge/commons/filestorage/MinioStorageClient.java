package com.wisdge.commons.filestorage;

import com.wisdge.utils.StringUtils;
import io.minio.GetObjectArgs;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.BeanUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * minio客户端
 */
@Slf4j
public class MinioStorageClient implements IFileStorageClient {

    /**
     * 访问的地址和端口
     */
    private String endpoint;
    /**
     * 访问的accessKey
     */
    private String accessKey;
    /**
     * 访问oss的accessSecret
     */
    private String accessSecret;
    /**
     * 用户存储的桶名称
     */
    private String bucketName;
    /**
     * 分区
     */
    private String region;
    /**
     * 是否为https
     */
    private boolean isHttps;
    /**
     * 客户端集合
     */
    private List<MinioClient> minioClientList;
    /**
     * 索引下标
     */
    private AtomicInteger indexAtomic = new AtomicInteger(0);

    @SneakyThrows
    @Override
    public void init() {
        log.debug("init MinIoStorageClient oos endpoint:{}, accessKey: {}, secretSecret: {}, bucketName:{}, region:{}",
                new Object[]{this.endpoint, this.accessKey, this.accessSecret, this.bucketName, this.region});
        minioClientList = new ArrayList<>();
        String[] arr = endpoint.split(",");
        for (String url : arr) {
            if (StringUtils.isBlank(url)) {
                continue;
            }
            String endpoint1 = url;
            if (!endpoint1.startsWith("http://") || !endpoint1.startsWith("https://")) {
                endpoint1 = isHttps ? "https://" : "http://" + url;
            }
            MinioClient minIoClient = new MinioClient();
            BeanUtils.copyProperties(this, minIoClient);
            io.minio.MinioClient minioClient = io.minio.MinioClient.builder().endpoint(endpoint1)
                    .credentials(this.accessKey, this.accessSecret).build();
            minIoClient.setEndpoint(endpoint1);
            minIoClient.setAvailable(true);
            minIoClient.setMinioClient(minioClient);
            minioClientList.add(minIoClient);
        }
    }

    /**
     * 获取可用客户端
     *
     * @return
     */
    private MinioClient getClient() {
        MinioClient client = null;
        long begintime = System.currentTimeMillis();
        do {
            if (indexAtomic.get() >= minioClientList.size()) {
                indexAtomic.set(0);
            }
            client = minioClientList.get(indexAtomic.getAndIncrement());
        } while ((client == null || !client.isAvailable()) && (System.currentTimeMillis() - begintime) < 6000);
        if (client == null) {
            throw new RuntimeException("没有可用的minio服务器，请联系管理员!");
        }
        return client;
    }

    @Override
    public String getRemoteRoot() {
        return null;
    }

    @Override
    public String save(String filePath, byte[] bytes) throws Exception {
        log.debug("save filePath: {}", filePath);
        if (StringUtils.isBlank(filePath) || bytes == null || bytes.length <= 0) {
            return null;
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        InputStream bis = null;
        MinioClient client = getClient();
        try {
            bis = new ByteArrayInputStream(bytes);
            ObjectWriteResponse objectWriteResponse = client.getMinioClient().putObject(
                    PutObjectArgs.builder().bucket(this.bucketName).object(filePath).stream(
                            bis, bis.available(), -1).build());
            if (objectWriteResponse == null) {
                throw new NotImplementedException("上传文件失败");
            }
        } catch (ErrorResponseException | IOException e) {
            client.setAvailable(false);
            throw e;
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return filePath;
    }

    @Override
    public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
        log.debug("saveStream filePath: {}", filePath);
        MinioClient client = getClient();
        try {
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            if (size == 0) {
                size = inputStream.available();
            }
            ObjectWriteResponse objectWriteResponse = client.getMinioClient().putObject(
                    PutObjectArgs.builder().bucket(this.bucketName).object(filePath).stream(
                            inputStream, size, -1).build());
            if (objectWriteResponse == null) {
                throw new NotImplementedException("上传文件失败");
            }
        } catch (ErrorResponseException | IOException e) {
            client.setAvailable(false);
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return filePath;
    }

    @Override
    public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener iProgressListener) throws Exception {
        return this.saveStream(filePath, inputStream, size);
    }


    @Override
    public byte[] retrieve(String filePath) throws Exception {
        log.debug("retrieve filePath: {}", filePath);
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        InputStream stream = null;
        byte[] buf = null;
        MinioClient client = getClient();
        try {
            stream = client.getMinioClient().getObject(GetObjectArgs.builder().bucket(this.bucketName).object(filePath).build());
            buf = this.toByteArray(stream);
        } catch (ErrorResponseException | IOException e) {
            client.setAvailable(false);
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return buf;
    }

    @Override
    public void retrieveStream(String filePath, IFileExecutor executor) throws Exception {
        log.debug("retrieveStream filePath: {}", filePath);
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        InputStream stream = null;
        MinioClient client = getClient();
        try {
            stream = client.getMinioClient().getObject(GetObjectArgs.builder().bucket(this.bucketName).object(filePath).build());
            FileMetadata metadata = new FileMetadata();
            metadata.setContentLength(stream.available());
            log.debug("into retrieverStream");
            executor.execute(stream, metadata);
        } catch (ErrorResponseException | IOException e) {
            client.setAvailable(false);
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Override
    public void delete(String filePath) throws Exception {
        log.warn("delete filePath: {}", filePath);
        MinioClient client = getClient();
        try {
            client.getMinioClient().removeObject(RemoveObjectArgs.builder().bucket(this.bucketName).object(filePath).build());
        } catch (ErrorResponseException | IOException e) {
            client.setAvailable(false);
            throw e;
        }
    }


    @Override
    public boolean isSecurity() {
        return false;
    }

    @Override
    public void destroy() {

    }

    /**
     * 读取输入流中的数据
     *
     * @param in
     * @return
     * @throws Exception
     */
    private byte[] toByteArray(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Throwable var3 = null;
        try {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();
        } catch (Throwable var16) {
            var3 = var16;
            throw var16;
        } finally {
            if (out != null) {
                if (var3 != null) {
                    try {
                        out.close();
                    } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                    }
                } else {
                    out.close();
                }
            }
        }
    }


    class MinioClient {
        /**
         * minio客户端
         */
        private io.minio.MinioClient minioClient;

        /**
         * 是否可用
         */
        private boolean available;

        /**
         * 访问的地址和端口
         */
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public io.minio.MinioClient getMinioClient() {
            return minioClient;
        }

        public void setMinioClient(io.minio.MinioClient minioClient) {
            this.minioClient = minioClient;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
    }

    public List<MinioClient> getMinioClientList() {
        return minioClientList;
    }

    public void setMinioClientList(List<MinioClient> minioClientList) {
        this.minioClientList = minioClientList;
    }

    public AtomicInteger getIndexAtomic() {
        return indexAtomic;
    }

    public void setIndexAtomic(AtomicInteger indexAtomic) {
        this.indexAtomic = indexAtomic;
    }

    @Override
    public String toString() {
        return "MinIoStorageClient{" +
                "endpoint='" + endpoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", accessSecret='" + accessSecret + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", region='" + region + '\'' +
                ", isHttps=" + isHttps +
                ", minioClientList=" + minioClientList +
                ", indexAtomic=" + indexAtomic +
                '}';
    }

}
