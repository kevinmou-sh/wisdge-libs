package com.wisdge.common.filestorage;

import com.wisdge.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class LocalStorage implements IFileStorageClient {
	private String remoteRoot;

	@Override
	public String getRemoteRoot() {
		return remoteRoot;
	}
	
	public void setRemoteRoot(String remoteRoot) {
		this.remoteRoot = remoteRoot;
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		String encoding = System.getProperty("file.encoding");
		filepath = encodingFilename(filepath, encoding);
		
		String path = FilenameUtils.getFullPathNoEndSeparator(filepath);
		FileUtils.forceMkdir(new File(path));
		File file = new File(filepath);
		if (!file.exists() && !file.createNewFile()) {
			throw new FileException("Can not create target file");
		}
		if (! file.setWritable(true, false)) {
			throw new FileException("Can not set file writable");
		}
		if (file.canWrite()) {
			FileUtils.writeByteArrayToFile(file, data);
			return "";
		} else {
			throw new FileException("Can not set file writable");
		}
	}

	@Override
	public String saveStream(String filepath, InputStream inputStream, long size) throws Exception {
		String encoding = System.getProperty("file.encoding");
		filepath = encodingFilename(filepath, encoding);

		String path = FilenameUtils.getFullPathNoEndSeparator(filepath);
		FileUtils.forceMkdir(new File(path));
		File file = new File(filepath);
		if (!file.exists() && !file.createNewFile()) {
			throw new FileException("Can not create target file");
		}
		if (! file.setWritable(true, false)) {
			throw new FileException("Can not set file writable");
		}
		if (file.canWrite()) {
			FileUtils.copyInputStreamToFile(inputStream, file);
			return "";
		} else {
			throw new FileException("Can not set file writable");
		}
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		String encoding = System.getProperty("file.encoding");
		filepath = encodingFilename(filepath, encoding);
		return FileUtils.readFileToByteArray(new File(filepath));
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		FileMetadata metadata = new FileMetadata();
		String encoding = System.getProperty("file.encoding");
		filepath = encodingFilename(filepath, encoding);
		File file = new File(filepath);
		try (InputStream is = new FileInputStream(file)) {
			metadata.setContentLength(file.length());
			metadata.setLastModified(file.lastModified());
			executor.execute(is, metadata);
		}
	}

	@Override
	public void delete(String filepath) throws Exception {
		String encoding = System.getProperty("file.encoding");
		filepath = encodingFilename(filepath, encoding);
		FileUtils.deleteQuietly(new File(filepath));
	}

	private String encodingFilename(String filename, String encoding) throws UnsupportedEncodingException {
		if (encoding.equalsIgnoreCase("UTF-8"))
			return filename;
		return new String(filename.getBytes(encoding));
	}

}
