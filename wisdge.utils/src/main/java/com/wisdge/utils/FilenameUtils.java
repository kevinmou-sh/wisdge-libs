package com.wisdge.utils;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.regex.Pattern;

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

    /**
     * 对文件名过滤特殊字符
     * @param filename
     * @return
     */
    public static String escapePath(String filename) {
        if (StringUtils.isEmpty(filename))
            return "";

        return filename.trim().replace(" ", "_")
                .replace("-", "_")
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
                .replace(":", "");
    }

    /**
     * 将通配符表达式转化为正则表达式
     * @param regexString String
     * @return Pattern
     */
    private static Pattern getRegex(String regexString) {
        char[] chars = regexString.toCharArray();
        int len = chars.length;
        StringBuilder sb = new StringBuilder();
        boolean preX = false;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '*') {//遇到*字符
                if (preX) {//如果是第二次遇到*，则将**替换成.*
                    sb.append(".*");
                    preX = false;
                } else if (i + 1 == len) {//如果是遇到单星，且单星是最后一个字符，则直接将*转成[^/]*
                    sb.append("[^/]*");
                } else {//否则单星后面还有字符，则不做任何动作，下一把再做动作
                    preX = true;
                    continue;
                }
            } else {//遇到非*字符
                if (preX) {//如果上一把是*，则先把上一把的*对应的[^/]*添进来
                    sb.append("[^/]*");
                    preX = false;
                }
                if (chars[i] == '?') {//接着判断当前字符是不是?，是的话替换成.
                    sb.append('.');
                } else {//不是?的话，则就是普通字符，直接添进来
                    sb.append(chars[i]);
                }
            }
        }
        return Pattern.compile(sb.toString());
    }

    /**
     * 判断文件是否包含在路径描述中（适用通配符）
     * @param pathRegex String 路径通配符描述
     * @param file String 文件名（包含路径）
     * @return boolean
     */
    public static boolean pathMatches(String pathRegex, String file) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pathRegex);
        Path path = Paths.get(file);
        return matcher.matches(path);
    }
}
