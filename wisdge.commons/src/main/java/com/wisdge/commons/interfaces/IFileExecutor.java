package com.wisdge.commons.interfaces;

import com.wisdge.commons.filestorage.FileMetadata;

import java.io.InputStream;

public interface IFileExecutor {
    void execute(InputStream inputStream, FileMetadata metadata);
}
