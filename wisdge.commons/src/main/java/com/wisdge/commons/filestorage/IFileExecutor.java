package com.wisdge.commons.filestorage;

import java.io.InputStream;

public interface IFileExecutor {
    void execute(InputStream inputStream, FileMetadata metadata);
}
