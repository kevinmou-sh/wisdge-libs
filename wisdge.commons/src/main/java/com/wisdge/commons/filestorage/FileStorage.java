package com.wisdge.commons.filestorage;

import com.wisdge.commons.interfaces.IFileDetector;
import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import com.wisdge.dataservice.Result;
import com.wisdge.utils.FilenameUtils;
import com.wisdge.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
public class FileStorage {
    public static final String DEFAULT_STORAGE = "default";
    private static final String FILE_STORAGE_NOT_EXIST = "文件服务{0}未配置";
    private static final String FILE_NOT_ACCEPT = "上传的文件类型{0}未被接受";
    private static final String FILE_FORBIDDEN = "上传的文件类型{0}被禁止";
    private static final String FILE_NOT_SAFE = "监测到不合法的文件内容";

    private Map<String, IFileStorageClient> fileStorages = new HashedMap();
    private Set<String> forbidden;
    private Set<String> accept;
    private IFileDetector fileDetector;

    public FileStorage(Set<String> forbidden, Set<String> accept) {
        this(forbidden, accept, null);
    }

    public FileStorage(Set<String> forbidden, Set<String> accept, IFileDetector fileDetector) {
        this.forbidden = forbidden;
        this.accept = accept;
        this.fileDetector = fileDetector;
    }

    public void addFileStorage(String key, IFileStorageClient fileStorageClient) {
        fileStorages.put(key, fileStorageClient);
    }

    public IFileStorageClient getFileStorage(String key) {
        return fileStorages.get(key);
    }

    /**
     * 对文件名过滤特殊字符
     * @param filename String 文件名
     * @return String
     */
    public String filterFilename(String filename) {
        if (StringUtils.isEmpty(filename))
            return "";

        return filename.trim().replace(" ", "_")
                .replace("-", "_")
                .replace("\\", "")
                .replace("/", "")
                .replace("+", "")
                .replace("#", "")
                .replace("%", "")
                .replace("&", "")
                .replace("?", "")
                .replace("*", "")
                .replace("@", "")
                .replace("=", "")
                .replace("..", "")
                .replace(":", "");
    }

    /**
     * 对文件路径过滤特殊字符
     * @param filepath String 文件路径
     * @return
     */
    public String filterFilepath(String filepath) {
        if (StringUtils.isEmpty(filepath))
            return "";

        return filepath.trim().replace(" ", "_")
                .replace("\\", "/")
                .replace("+", "")
                .replace("#", "")
                .replace("%", "")
                .replace("&", "")
                .replace("?", "")
                .replace("*", "")
                .replace("@", "")
                .replace("=", "")
                .replace("..", "")
                .replace(":", "")
                .replace("-", "_");
    }

    public boolean isSecurity(String fsKey) {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient != null)
            return fileStorageClient.isSecurity();
        return false;
    }

    /**
     * 保存文件到文件服务
     * @param uploadPath
     * 	String 文件上传的路径
     * @param original
     * 	String 文件原始名
     * @param filename
     * 	String 文件保存名
     * @param inputStream
     * 	InputStream 文件流
     * @param size
     * 	long 文件大小
     * @return
     * 	Result对象
     */
    public Result saveStream(String uploadPath, String original, String filename, InputStream inputStream, long size, IProgressListener progressListener) {
        return saveStream(DEFAULT_STORAGE, uploadPath, original, filename, inputStream, size, progressListener);
    }

    /**
     * 保存文件到文件服务
     * @param fsKey
     *  String 文件服务的Key
     * @param uploadPath
     * 	String 文件上传的路径
     * @param original
     * 	String 文件原始名
     * @param filename
     * 	String 文件保存名
     * @param inputStream
     * 	InputStream 文件流
     * @param size
     * 	long 文件大小
     * @return
     * 	Result对象
     */
    public Result saveStream(String fsKey, String uploadPath, String original, String filename, InputStream inputStream, long size, IProgressListener progressListener) {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            return new Result(Result.ERROR, MessageFormat.format(FILE_STORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filename = filterFilename(filename);
        uploadPath = filterFilepath(uploadPath);

        // Get final remote file path
        String requestRemote = concat(uploadPath, filename);
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, requestRemote);
        requestRemote = fsKey.equals(DEFAULT_STORAGE) ? requestRemote : (fsKey + "@" + requestRemote);

        try {
            /**
             * 过滤文件上传白名单
             */
            if (!isAcceptFile(finalRemote))
                return new Result(Result.ERROR, MessageFormat.format(FILE_NOT_ACCEPT, FilenameUtils.getExtension(finalRemote)));
            /**
             * 过滤文件上传黑名单，黑名单的优先等级要高于白名单
             */
            if (isForbiddenFile(filename))
                return new Result(Result.ERROR, MessageFormat.format(FILE_FORBIDDEN, FilenameUtils.getExtension(finalRemote)));
            /**
             * 如果注入了文件内容安全检测器的，则需要对文件内容进行安全性检测
             */
            if (fileDetector != null) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    // Code simulating the copy
                    // You could alternatively use NIO
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                    // Open new InputStreams using recorded bytes
                    // Can be repeated as many times as you wish
                    try (
                        InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                        InputStream is2 = new ByteArrayInputStream(baos.toByteArray())
                    ) {
                        if (!fileDetector.isSafe(is1))
                            return new Result(Result.ERROR, FILE_NOT_SAFE);

                        log.info("[{}] Save file to {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
                        String newPath = fileStorageClient.saveStream(finalRemote, is2, size, progressListener);
                        if (StringUtils.isNotEmpty(newPath))
                            requestRemote = fsKey.equals(DEFAULT_STORAGE) ? newPath : (fsKey + "@" + newPath);
                        return new Result(Result.SUCCESS, original, requestRemote);
                    }
                }
            } else {
                log.info("[{}] Save file to {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
                String newPath = fileStorageClient.saveStream(finalRemote, inputStream, size, progressListener);
                if (StringUtils.isNotEmpty(newPath))
                    requestRemote = fsKey.equals(DEFAULT_STORAGE) ? newPath : (fsKey + "@" + newPath);
                return new Result(Result.SUCCESS, original, requestRemote);
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new Result(Result.ERROR, e.getLocalizedMessage());
        }
    }

    /**
     * 判断文件路径包含的文件类型是否被允许
     * @param filePath String 文件路径
     * @return
     */
    public boolean isAcceptFile(String filePath) {
        return isAcceptExtension(FilenameUtils.getExtension(filePath));
    }

    /**
     * 判断文件后缀是否被允许
     * @param extension String 文件后缀
     * @return
     */
    public boolean isAcceptExtension(String extension) {
        if (accept == null || accept.isEmpty())
            return true;

        for(String acceptExt : accept) {
            if (acceptExt.equalsIgnoreCase(extension))
                return true;
        }
        return false;
    }

    /**
     * 判断文件路径包含的文件类型是否被禁止
     * @param filePath String 文件路径
     * @return
     */
    public boolean isForbiddenFile(String filePath) {
        return isForbiddenExtension(FilenameUtils.getExtension(filePath));
    }

    /**
     * 判断文件后缀是否被禁止
     * @param extension String 文件后缀
     * @return
     */
    public boolean isForbiddenExtension(String extension) {
        if (forbidden == null || forbidden.isEmpty())
            return false;

        for(String forbiddenExt : forbidden) {
            if (forbiddenExt.equalsIgnoreCase(extension))
                return true;
        }
        return false;
    }

    public void retrieveStream(String fsKey, String filepath, IFileExecutor executor) throws Exception {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            throw new NullPointerException(MessageFormat.format(FILE_STORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filepath = filterFilepath(filepath);
        // Get final remote file path
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, filepath);
        log.info("[{}] Retrieve file from {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
        fileStorageClient.retrieveStream(finalRemote, executor);
    }

    /**
     * 保存文件到文件服务
     * @param fsKey
     * 	String 文件服务的key
     * @param uploadPath
     * 	String 文件上传的路径
     * @param original
     * 	String 文件原始名
     * @param filename
     * 	String 文件保存名
     * @param data
     * 	byte[] 文件内容
     * @return
     * 	Result对象
     */
    public Result saveFile(String fsKey, String uploadPath, String original, String filename, byte[] data) {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            throw new NullPointerException(MessageFormat.format(FILE_STORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filename = filterFilename(filename);
        uploadPath = filterFilepath(uploadPath);
        // Get final remote file path
        String requestRemote = concat(uploadPath, filename);
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, requestRemote);
        requestRemote = fsKey.equals(DEFAULT_STORAGE) ? requestRemote : (fsKey + "@" + requestRemote);

        try {
            if (!isAcceptFile(finalRemote))
                return new Result(Result.ERROR, MessageFormat.format(FILE_NOT_ACCEPT, FilenameUtils.getExtension(finalRemote)));
            if (isForbiddenFile(filename))
                return new Result(Result.ERROR, MessageFormat.format(FILE_FORBIDDEN, FilenameUtils.getExtension(finalRemote)));
            if (fileDetector != null && !fileDetector.isSafe(data))
                return new Result(Result.ERROR, FILE_NOT_SAFE);

            log.info("[{}] Save file to {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
            String newPath = fileStorageClient.save(finalRemote, data);
            if (! StringUtils.isEmpty(newPath)) {
                requestRemote = fsKey.equals(DEFAULT_STORAGE) ? newPath : (fsKey + "@" + newPath);
            }
            return new Result(Result.SUCCESS, original, requestRemote);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new Result(Result.ERROR, e.getLocalizedMessage());
        }
    }

    public byte[] retrieveFile(String fsKey, String filepath) throws Exception {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            throw new NullPointerException(MessageFormat.format(FILE_STORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filepath = filterFilepath(filepath);
        // Get final remote file path
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, filepath);
        log.info("[{}] Retrieve file from {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
        byte[] data = fileStorageClient.retrieve(finalRemote);

        if (data == null) { // 允许空内容文件，无需判断data.length == 0
            throw new FileNotFoundException(finalRemote);
        }
        return data;
    }

    public void deleteFile(String fsKey, String filepath) throws Exception {
        if (StringUtils.isEmpty(fsKey))
            fsKey = DEFAULT_STORAGE;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            throw new NullPointerException(MessageFormat.format(FILE_STORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filepath = filterFilepath(filepath);
        // Get final remote file path
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, filepath);
        log.info("[{}] Delete file from {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
        fileStorageClient.delete(finalRemote);
    }

    public String concat(String...path) {
        if (path == null || path.length == 0)
            return "";

        if (path.length == 1)
            return path[0];

        String fullpath = path[0].replace("\\", "/");
        for(int i=1; i<path.length; i++) {
            fullpath = concat2(fullpath, path[i].replace("\\", "/"));
        }
        return fullpath;
    }

    private String concat2(String path1, String path2) {
        if (StringUtils.isEmpty(path1))
            return path2;
        if (StringUtils.isEmpty(path2))
            return path1;

        if (path1.endsWith("/")) {
            if (path2.startsWith("/")) {
                return path1 + path2.substring(1);
            } else {
                return path1 + path2;
            }
        } else {
            if (path2.startsWith("/")) {
                return path1 + path2;
            } else {
                return path1 + "/" + path2;
            }
        }
    }

    private String getRemoteRoot(String remoteRoot) {
        if (StringUtils.isEmpty(remoteRoot))
            remoteRoot = "/";
        else {
            if (! remoteRoot.startsWith("/"))
                remoteRoot = "/" + remoteRoot;
        }
        return remoteRoot;
    }
}
