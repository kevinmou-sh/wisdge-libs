package com.wisdge.web.upload;

import java.util.HashMap;
import java.util.Map;

public class BeanController {
	private static BeanController beanControler;
	private Map<String, FileUploadStatus> statusMap = new HashMap<String, FileUploadStatus>();

	public synchronized static BeanController getDefault() {
		if (beanControler == null)
			beanControler = new BeanController();
		
		return beanControler;
	}

	/**
	 * 取得相应FileUploadStatus类对象
	 */
	public static FileUploadStatus getUploadStatus(String ulpId) {
		return getDefault().statusMap.get(ulpId);
	}

	/**
	 * 存储FileUploadStatus类对象
	 */
	public static void setUploadStatus(FileUploadStatus status) {
		getDefault().statusMap.put(status.getUlpId(), status);
	}

	/**
	 * 删除FileUploadStatus类对象
	 */
	public static void removeUploadStatus(String ulpId) {
		getDefault().statusMap.remove(ulpId);
	}
}
