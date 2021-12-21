package com.wisdge.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordUtilsTester {

    @Test
    public void test() throws PasswordInvalidException {
        String password = "Letmein_0308~";
        List<String> excludes = new ArrayList<>();
        excludes.add("kevin");
        PasswordMatchResult result = PasswordUtils.match(password, 8, 20, PasswordUtils.RULE_ALLCASE |
                PasswordUtils.RULE_DIGIT |
                PasswordUtils.RULE_SPECIAL |
                PasswordUtils.RULE_CONTINUOUS_NATURE |
                PasswordUtils.RULE_CONTINUOUS_KEYBOARD, excludes);
        System.out.println(result);

        String regexString = "^(?=.*[a-z])(?=.*[A-Z])(?=.*d){8,16}$";
        Pattern pattern = Pattern.compile(regexString);
        System.out.println(pattern.matches(regexString, password));
    }

}
