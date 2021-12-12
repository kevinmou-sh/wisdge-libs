package com.wisdge.utils;

import java.text.Normalizer;

public class LogUtils {

    public static Object forging(Object object) {
        String encode = Normalizer.normalize(object.toString(), Normalizer.Form.NFKC);
        encode.replace("%0d", "")
                .replace("\r", "")
                .replace("%0a", "")
                .replace("\n", "");
        return encode;
    }
}
