package com.wisdge.web.upload;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wisdge.dataservice.Result;

@Slf4j
@Controller
public class WisdgeUploadController {
	
	@GetMapping("/upload/status")
	@ResponseBody
	public Result uploadStatusQuery(HttpServletRequest request) {
		FileUploadStatus status = FileUploadListener.getStatusBean(request);
		if (status == null)
			return new Result(Result.ERROR, "Missing fileUploadStatus ulpid=" + request.getParameter("ulpid"));
		return new Result(Result.SUCCESS, "", status);
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
