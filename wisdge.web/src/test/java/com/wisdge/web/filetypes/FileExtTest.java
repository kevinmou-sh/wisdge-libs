package com.wisdge.web.filetypes;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.io.IOException;

@Slf4j
public class FileExtTest {

    @Test
    public void test() throws IOException {
        byte[] data = FileExt.getImgByExt("docx");
        System.out.println("File type image size:" + data.length);
    }
}
