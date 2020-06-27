package com.wisdge.dataservice.xhr;

import org.apache.http.HttpResponse;

public interface IResponseHandler {
	public void doHandle(HttpResponse httpResponse);
}
