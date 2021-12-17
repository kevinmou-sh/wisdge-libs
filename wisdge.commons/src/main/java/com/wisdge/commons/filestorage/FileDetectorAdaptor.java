package com.wisdge.commons.filestorage;

import com.wisdge.commons.interfaces.IFileDetector;

import java.io.ByteArrayInputStream;

public abstract class FileDetectorAdaptor implements IFileDetector {
    public boolean isSafe(byte[] data) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            return isSafe(inputStream);
        }
    }
}
