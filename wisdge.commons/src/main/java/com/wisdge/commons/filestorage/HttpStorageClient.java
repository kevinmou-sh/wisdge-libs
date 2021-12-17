package com.wisdge.commons.filestorage;

import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import com.wisdge.dataservice.Result;
import com.wisdge.dataservice.utils.JSonUtils;
import com.wisdge.dataservice.xhr.XHRPoolService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class HttpStorageClient implements IFileStorageClient {
	@Autowired
	private XHRPoolService xhrService;

	private String remoteRoot;
	private String saveUrl;
	private String retrieveUrl;
	private String deleteUrl;
	private String inputField = "file";
	private String pathField = "path";
	private boolean security;

	@Override
	public void init(boolean security) {
		this.security = security;
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		HttpPost httpPost = new HttpPost(saveUrl);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody(inputField, data);
		builder.addTextBody(pathField, filepath);
		HttpEntity entity = builder.build();
		httpPost.setEntity(entity);

		String resultString = xhrService.post(httpPost);
		Result result = JSonUtils.read(resultString, Result.class);
		if (result.getCode() > 0) {
			return result.getValue().toString();
		} else {
			throw new FileException("File upload failed");
		}
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
		return saveStream(filePath, inputStream, size);
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		try (InputStream source = inputStream) {
			HttpPost httpPost = new HttpPost(saveUrl);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody(inputField, source);
			builder.addTextBody(pathField, filePath);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);

			String resultString = xhrService.post(httpPost);
			Result result = JSonUtils.read(resultString, Result.class);
			if (result.getCode() > 0) {
				return result.getValue().toString();
			} else {
				throw new FileException("File upload failed");
			}
		}
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put(inputField, filepath);
		return xhrService.postForBytes(retrieveUrl, params);
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		FileMetadata metadata = new FileMetadata();
		Map<String, Object> params = new HashMap<>();
		params.put(inputField, filepath);
		CloseableHttpResponse response = xhrService.postResponse(retrieveUrl, params);
		try (InputStream is = response.getEntity().getContent()) {
			metadata.setContentLength(response.getEntity().getContentLength());
			executor.execute(is, metadata);
		}
	}

	@Override
	public void delete(String filepath) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put(inputField, filepath);
		Result result = JSonUtils.read(xhrService.post(deleteUrl, params), Result.class);
		if (result.getCode() <= 0) {
			throw new FileException("File delete failed");
		}
	}

	@Override
	public void destroy() {
	}

}
