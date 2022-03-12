package com.wisdge.utils;

import org.junit.Test;

public class MaskUtilsTester {

    @Test
    public void test() {
        System.out.println(MaskUtils.maskMobile("18621991973"));
        System.out.println(MaskUtils.maskEmail("kevinmou@wisdge.com"));
        System.out.println(MaskUtils.maskIDCard("36213019870314102x"));
        System.out.println(MaskUtils.maskTel("02164516261"));
        System.out.println(MaskUtils.maskBankCard("542763987183563"));
        System.out.println(MaskUtils.maskName("李宁"));
    }
}
