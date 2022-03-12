package com.wisdge.utils;

import org.junit.Test;

public class SnowflakeWorkerIdTester {

    @Test
    public void test() {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 1);
        idWorker.init();
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(id);
        }
    }
}
