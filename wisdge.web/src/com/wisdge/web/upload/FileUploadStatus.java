package com.wisdge.web.upload;

import java.util.HashMap;
import java.util.Map;

public class FileUploadStatus {
	// 文件上传进度查询id
	private String ulpId;
	// 上传总量
	private long uploadTotalSize = 0;
	// 读取上传总量
	private long readTotalSize = 0;
	// 当前上传文件号
	private int currentUploadFileNum = 0;
	// 成功读取上传文件数
	private int successUploadFileCount = 0;
	// 状态
	private String status = "";
	// 处理起始时间
	private long processStartTime = 0l;
	// 处理终止时间
	private long processEndTime = 0l;
	// 处理执行时长（毫秒）
	private long processElapseTime = 0l;
	// 取消上传
	private boolean cancel = false;

	public FileUploadStatus() {

	}

	public boolean getCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public long getProcessElapseTime() {
		return processElapseTime;
	}

	public void setProcessElapseTime(long processElapseTime) {
		this.processElapseTime = processElapseTime;
	}

	public long getProcessEndTime() {
		return processEndTime;
	}

	public void setProcessEndTime(long processEndTime) {
		this.processEndTime = processEndTime;
	}

	public long getProcessStartTime() {
		return processStartTime;
	}

	public void setProcessStartTime(long processStartTime) {
		this.processStartTime = processStartTime;
	}

	public long getReadTotalSize() {
		return readTotalSize;
	}

	public void setReadTotalSize(long readTotalSize) {
		this.readTotalSize = readTotalSize;
	}

	public int getSuccessUploadFileCount() {
		return successUploadFileCount;
	}

	public void setSuccessUploadFileCount(int successUploadFileCount) {
		this.successUploadFileCount = successUploadFileCount;
	}

	public int getCurrentUploadFileNum() {
		return currentUploadFileNum;
	}

	public void setCurrentUploadFileNum(int currentUploadFileNum) {
		this.currentUploadFileNum = currentUploadFileNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getUploadTotalSize() {
		return uploadTotalSize;
	}

	public void setUploadTotalSize(long uploadTotalSize) {
		this.uploadTotalSize = uploadTotalSize;
	}

	public String getUlpId() {
		return ulpId;
	}

	public void setUlpId(String ulpId) {
		this.ulpId = ulpId;
	}

	public String toJSon() {
		/* 速率，单位 K/S */
		float rate = (readTotalSize/1024f) / (processElapseTime/1000f);
		/* 剩余时间，单位:秒 */
		float leftTime = ((uploadTotalSize - readTotalSize)/1024f) / rate;
		StringBuffer strJSon = new StringBuffer();
		strJSon.append("{ulpId:").append(ulpId)
			.append(",").append("totalSize:").append(uploadTotalSize)
			.append(",").append("readSize:").append(readTotalSize)
			.append(",").append("percent:").append((readTotalSize * 100) / uploadTotalSize)
			.append(",").append("currentFileNum:").append(currentUploadFileNum)
			.append(",").append("successFileCount:").append(successUploadFileCount)
			.append(",").append("status:'").append(status).append("'")
			.append(",").append("startTime:").append(processStartTime)
			.append(",").append("endTime:").append(processEndTime)
			.append(",").append("elapseTime:").append(processElapseTime)
			.append(",").append("rate:").append(rate)
			.append(",").append("leftTime:").append(leftTime)
			.append(",").append("cancel:").append(cancel).append("}");
		return strJSon.toString();
	}
	
	public Map<String, Object> toMap() {
		/* 速率，单位 K/S */
		float rate = (readTotalSize/1024f) / (processElapseTime/1000f);
		/* 剩余时间，单位:秒 */
		float leftTime = ((uploadTotalSize - readTotalSize)/1024f) / rate;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ulpId", ulpId);
		map.put("totalSize", uploadTotalSize);
		map.put("readSize", readTotalSize);
		map.put("percent", (readTotalSize * 100) / uploadTotalSize);
		map.put("currentFileNum", currentUploadFileNum);
		map.put("successFileCount", successUploadFileCount);
		map.put("status", status);
		map.put("startTime", processStartTime);
		map.put("endTime", processEndTime);
		map.put("elapseTime", processElapseTime);
		map.put("rate", rate);
		map.put("leftTime", leftTime);
		map.put("cancel", cancel);
		return map;
	}

}
