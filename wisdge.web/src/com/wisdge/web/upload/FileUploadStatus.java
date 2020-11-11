package com.wisdge.web.upload;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class FileUploadStatus implements Serializable {
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
	private long processStartTime = 0L;
	// 处理终止时间
	private long processEndTime = 0L;
	// 处理执行时长（毫秒）
	private long processElapseTime = 0L;
	// 取消上传
	private boolean cancel = false;
}
