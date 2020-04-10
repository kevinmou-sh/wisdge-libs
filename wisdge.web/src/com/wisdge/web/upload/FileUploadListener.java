package com.wisdge.web.upload;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.ProgressListener;

public class FileUploadListener implements ProgressListener {
	private HttpServletRequest request = null;

	public FileUploadListener(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 从request中取出FileUploadStatus Bean
	 */
	public static FileUploadStatus getStatusBean(HttpServletRequest request) {
		return BeanController.getUploadStatus(request.getParameter("ulpid"));
	}

	/**
	 * 把FileUploadStatus Bean保存到类控制器BeanControler
	 */
	public static void saveStatusBean(FileUploadStatus statusBean) {
		BeanController.setUploadStatus(statusBean);
	}

	@Override
	public void update(long pBytesRead, long pContentLength, int pItems) {
		FileUploadStatus statusBean = getStatusBean(request);
		statusBean.setUploadTotalSize(pContentLength);

		if (pContentLength == -1) {
			// 读取完成
			statusBean.setStatus("完成对" + pItems + "个文件的读取:读取了 " + pBytesRead + " bytes.");
			statusBean.setReadTotalSize(pBytesRead);
			statusBean.setSuccessUploadFileCount(pItems);
			statusBean.setProcessEndTime(System.currentTimeMillis());
			statusBean.setProcessElapseTime(statusBean.getProcessEndTime() - statusBean.getProcessStartTime());
		} else {
			// 读取中
			statusBean.setStatus("当前正在处理第" + pItems + "个文件:已经读取了 " + pBytesRead + " / " + pContentLength + " bytes.");
			statusBean.setReadTotalSize(pBytesRead);
			statusBean.setCurrentUploadFileNum(pItems);
			statusBean.setProcessElapseTime(System.currentTimeMillis() - statusBean.getProcessStartTime());
		}
		saveStatusBean(statusBean);
	}

}
