package com.wisdge.commons.filestorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private long contentLength;
    private long lastModified;
    private String contentType;
    private String downloadURL;

    public FileMetadata(long contentLength) {
        this.contentLength = contentLength;
    }
}
