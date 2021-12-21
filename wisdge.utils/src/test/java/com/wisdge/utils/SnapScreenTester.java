package com.wisdge.utils;

import org.junit.Test;

import java.awt.*;
import java.io.IOException;

public class SnapScreenTester {
    @Test
    public void test() throws IOException, AWTException {
        SnapScreen.shot("/Users/kevinmou/Documents/temp/test", SnapScreen.FORMAT_PNG);
    }
}
