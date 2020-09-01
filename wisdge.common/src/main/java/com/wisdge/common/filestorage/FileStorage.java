package com.wisdge.common.filestorage;

import com.wisdge.dataservice.Result;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStream;

@Slf4j
public class FileStorage {

    @Autowired
    private IFileStorageClient fileStorageClient;

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
    public Result saveStream(String uploadPath, String original, String filename, InputStream inputStream, long size) {
        // Replace specially char at filename and uploadPath
        filename = filterFilename(filename);
        uploadPath = filterFilepath(uploadPath);

        // Get final remote file path
        String requestRemote = concat(uploadPath, filename);
        String remoteRoot = getRemoteRoot(fileStorageClient.getRemoteRoot());
        String finalRemote = concat(remoteRoot, requestRemote);

        try {
            log.info("Save file to {}: {}", fileStorageClient.getClass().getSimpleName(), finalRemote);
            String newPath = fileStorageClient.saveStream(finalRemote, inputStream, size);
            return new Result(Result.SUCCESS, original, newPath);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return new Result(Result.ERROR);
        }
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
