package com.wisdge.web.upload;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wisdge.dataservice.Result;

@Controller
@RequestMapping(value="/upload")
public class WisdgeUploadController {
	
	@RequestMapping(value="/status", method = RequestMethod.GET)
	@ResponseBody
	public Result uploadStatusQuery(HttpServletRequest request) {
		FileUploadStatus status = FileUploadListener.getStatusBean(request);
		if (status == null)
			return new Result(Result.ERROR, "Missing fileUploadStatus ulpid=" + request.getParameter("ulpid"));
			
		return new Result(Result.SUCCESS, "", status.toMap());
	}
	
	@RequestMapping(value="/cancel", method = RequestMethod.GET)
	@ResponseBody
	public Result uploadCancel(HttpServletRequest request) {
		FileUploadStatus status = FileUploadListener.getStatusBean(request);
		status.setCancel(true); 
		FileUploadListener.saveStatusBean(status); 
		return uploadStatusQuery(request); 
	}
}
