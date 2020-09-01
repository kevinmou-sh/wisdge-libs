package com.wisdge.common.filestorage;

import com.wisdge.dataservice.Result;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;

@Slf4j
public class FileStorage {
    private final String FILESTORAGE_NOT_EXIST = "文件服务{0}未配置";
    private final String FIELD_DEFAULT = "default";
    private Map<String, IFileStorageClient> fileStorages;

    public Map<String, IFileStorageClient> getFileStorages() {
        return fileStorages;
    }

    public void setFileStorages(Map<String, IFileStorageClient> fileStorages) {
        this.fileStorages = fileStorages;
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
            fsKey = FIELD_DEFAULT;

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
    public Result save(String uploadPath, String original, String filename, InputStream inputStream, long size) {
        return save(FIELD_DEFAULT, uploadPath, original, filename, inputStream, size);
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
    public Result save(String fsKey, String uploadPath, String original, String filename, InputStream inputStream, long size) {
        if (StringUtils.isEmpty(fsKey))
            fsKey = FIELD_DEFAULT;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            return new Result(Result.ERROR, MessageFormat.format(FILESTORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filename = filterFilename(filename);
        uploadPath = filterFilepath(uploadPath);

        // Get final remote file path
        String requestRemote = concat(uploadPath, filename);
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, requestRemote);

        try {
            log.info("[{}] Save file to {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
            String newPath = fileStorageClient.saveStream(finalRemote, inputStream, size);
            if (! fsKey.equals(FIELD_DEFAULT))
                newPath = fsKey + "@" + newPath;
            return new Result(Result.SUCCESS, original, newPath);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new Result(Result.ERROR, e.getMessage());
        }
    }

    public void retrive(String fsKey, String filepath, IFileExecutor executor) throws Exception {
        if (StringUtils.isEmpty(fsKey))
            fsKey = FIELD_DEFAULT;

        IFileStorageClient fileStorageClient = fileStorages.get(fsKey);
        if (fileStorageClient == null)
            throw new NullPointerException(MessageFormat.format(FILESTORAGE_NOT_EXIST, fsKey));

        // Replace specially char at filename and uploadPath
        filepath = filterFilepath(filepath);
        // Get final remote file path
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, filepath);
        log.info("[{}] Retrieve file from {}: {}", fsKey, fileStorageClient.getClass().getSimpleName(), finalRemote);
        fileStorageClient.retrieveStream(finalRemote, executor);
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
