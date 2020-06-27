package com.wisdge.utils;

public class FilenameUtils extends org.apache.commons.io.FilenameUtils {

    /**
     * 对文件名过滤特殊字符
     * @param filename
     * @return
     */
    public static String escape(String filename) {
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

}
