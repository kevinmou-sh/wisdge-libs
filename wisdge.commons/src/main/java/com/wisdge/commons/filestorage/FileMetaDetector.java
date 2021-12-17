package com.wisdge.commons.filestorage;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileMetaDetector {

    /**
     * 解析mp4文件时长
     * @param inputStream InputStream
     * @return long duration of mp4
     * @throws ImageProcessingException
     * @throws IOException
     * @throws MetadataException
     */
    public static long getMp4Duration(InputStream inputStream) throws ImageProcessingException, IOException, MetadataException {
        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
        Iterable<Directory> it = metadata.getDirectories();
        for (Directory d : it) {
            if (d.getName().equalsIgnoreCase("MP4")) {
                return d.getLong(260);// 获取duration
            }
        }
        return 0L;
    }

    /**
     * 解析mp4文件时长
     * @param mp4File File
     * @return long duration of mp4
     * @throws ImageProcessingException
     * @throws IOException
     * @throws MetadataException
     */
    public static long getMp4Duration(File mp4File) throws ImageProcessingException, IOException, MetadataException {
        try (
                InputStream inputStream = new FileInputStream(mp4File)
        ) {
            return getMp4Duration(inputStream);
        }
    }

    /**
     * 解析mp3文件时长
     * @param mp3File
     * @return
     * @throws InvalidDataException
     * @throws IOException
     * @throws UnsupportedTagException
     */
    public static long getMp3Duration(File mp3File) throws InvalidDataException, IOException, UnsupportedTagException {
        Mp3File mp3file = new Mp3File(mp3File);
        return mp3file.getLengthInSeconds();
    }
}
