package com.wisdge.commons;

import org.junit.Test;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

public class UtilsTest {

    @Test
    public void test() {
        String ignore = "js/app/**";
        String name = "js/app/ios.js";
        String name2 = "js/app/ios/a/b/c.js";

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + ignore);
        System.out.println(matcher.matches(Paths.get(name)));
        System.out.println(matcher.matches(Paths.get(name2)));
    }

}
