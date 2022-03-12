package com.wisdge.utils;

import org.junit.Test;

public class MaskUtils {
    public static final String ENCRYPT_DEF = "***"; //默认全掩码

    /**
     * 掩码手机号
     * @param mobile
     * @return String
     */
    public static String maskMobile(String mobile) {
        if (StringUtils.isEmpty(mobile) || mobile.length() <= 8)
            return mobile;

        return mobile.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
    }

    /**
     * 电话号码显示区号及末4位，中间用*号隐藏代替，如：010****4213
     *
     * @param telephone
     * @return String
     */
    public static String maskTel(String telephone) {
        if (StringUtils.isBlank(telephone))
            return telephone;

        int length = telephone.length();
        if (length > 8) {
            if (telephone.contains("-")) {
                String[] temp = telephone.split("-");
                return temp[0] + "****" + temp[1].substring(temp[1].length() - 4);
            } else {
                return telephone.substring(0, 3) + "****" + telephone.substring(length - 4);
            }
        } else {
            return "****" + telephone.substring(length - 4);
        }
    }

    /**
     * 身份证号显示首6末4位，中间用4个*号隐藏代替，如：421002****1012
     *
     * @param idCard
     * @return
     */
    public static String maskIDCard(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() < 11) {
            return idCard;
        }

        return wordMask(idCard, 6, 4, "*");
    }

    /**
     * 银行卡显示首6末4位，中间用4个*号隐藏代替，如：622202****4123
     *
     * @param cardNo
     * @return
     */
    public static String maskBankCard(String cardNo) {
        if(StringUtils.isBlank(cardNo) || cardNo.length() < 11) {
            return cardNo;
        }

        return wordMask(cardNo, 6, 4, "*");
    }

    /**
     * 三个字掩码，如：张晓明 如：张*明
     * 两个字掩码，如：小明 如：*明
     * 多个字掩码，如：张小明明 如：张**明
     *
     * @param name
     * @return
     */
    public static String maskName(String name) {
        if(StringUtils.isBlank(name) || name.length() == 1) {
            return name;
        }
        if (name.length() == 2) {
            return "*" + name.substring(1, 2);
        }

        return wordMask(name, 1, 1, "*");
    }

    /**
     * 掩码邮箱
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        if (StringUtils.isEmpty(email))
            return email;

        String[] temp = email.split("@");
        return wordMask(temp[0], 3, 0, "*") + "@" + temp[1];
    }

    /**
     * 对字符串进行脱敏处理
     *
     * @param word 被脱敏的字符
     * @param startLength 被保留的开始长度 前余n位
     * @param endLength 被保留的结束长度 后余n位
     * @param pad 填充字符
     * */
    public static String wordMask(String word, int startLength, int endLength, String pad)    {
        if (startLength + endLength > word.length()) {
            return org.apache.commons.lang3.StringUtils.leftPad("", word.length() - 1, pad);
        }

        String startStr = word.substring(0, startLength);
        String endStr = word.substring(word.length() - endLength);
        return startStr + StringUtils.leftPad("", word.length() - startLength - endLength, pad) + endStr;
    }
}
