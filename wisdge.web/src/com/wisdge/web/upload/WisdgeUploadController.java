package com.wisdge.web.upload;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wisdge.dataservice.Result;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class WisdgeUploadController {
	
	@GetMapping("/upload/status")
	@ResponseBody
	public Result uploadStatusQuery(HttpServletRequest request) {
		FileUploadStatus status = FileUploadListener.getStatusBean(request);
		if (status == null)
			return new Result(Result.ERROR, "Missing fileUploadStatus ulpid=" + request.getParameter("ulpid"));

		/* 速率，单位 K/S */
		float rate = (status.getReadTotalSize() / 1024f) / (status.getProcessElapseTime() / 1000f);
		/* 剩余时间，单位:秒 */
		float left = ((status.getUploadTotalSize() - status.getReadTotalSize())/1024f) / rate;
		/* 进度百分比 */
		float percent = (status.getReadTotalSize() * 100) / status.getUploadTotalSize();
		Map<String, Object> info = new HashMap<>();
		info.put("rate", rate);
		info.put("left", left);
		info.put("percent", percent);
		info.put("elapse", status.getProcessElapseTime() / 1000f);
		info.put("total", status.getUploadTotalSize() / 1024f);
		info.put("read", status.getReadTotalSize() / 1024f);
		return new Result(Result.SUCCESS, "", info);
	}
	
	@GetMapping("/upload/cancel")
	@ResponseBody
	public Result uploadCancel(HttpServletRequest request) {
		FileUploadStatus status = FileUploadListener.getStatusBean(request);
		status.setCancel(true); 
		FileUploadListener.saveStatusBean(status); 
		return uploadStatusQuery(request); 
	}
}
