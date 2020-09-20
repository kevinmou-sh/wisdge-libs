package com.wisdge.web.upload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class CommonsMultipartResolverEx extends CommonsMultipartResolver {
	private HttpServletRequest request;
	
	/**
	 * Constructor for use as bean. Determines the servlet container's
	 * temporary directory via the ServletContext passed in as through the
	 * ServletContextAware interface (typically by a WebApplicationContext).
	 * @see #setServletContext
	 * @see org.springframework.web.context.ServletContextAware
	 * @see org.springframework.web.context.WebApplicationContext
	 */
	public CommonsMultipartResolverEx() {
		super();
	}

	/**
	 * Constructor for standalone usage. Determines the servlet container's
	 * temporary directory via the given ServletContext.
	 * @param servletContext the ServletContext to use
	 */
	public CommonsMultipartResolverEx(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		upload.setSizeMax(-1);

		if (request != null) {
			String ulpId = request.getParameter("ulpid");
			if (! StringUtils.isEmpty(ulpId)) {
				FileUploadListener uploadProgressListener = new FileUploadListener(request);
				upload.setProgressListener(uploadProgressListener);
				FileUploadListener.saveStatusBean(initializeStatusBean(ulpId));
			} else {
			}
		}
		return upload;
	}

	private FileUploadStatus initializeStatusBean(String ulpId) {
		FileUploadStatus statusBean = new FileUploadStatus();
		statusBean.setUlpId(ulpId);
		statusBean.setStatus("正在准备处理...");
		statusBean.setUploadTotalSize(request.getContentLength());
		statusBean.setProcessStartTime(System.currentTimeMillis());
		return statusBean;
	}

	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
		this.request = request;// 获取到request
		return super.resolveMultipart(request);
	}

	/**
	 * 正常情况下直接转换MultipartHttpServletRequest
	 * 在JBoss下，需要手动再次resolve一下
	 * @param request
	 * @return
	 */
	public static MultipartHttpServletRequest getMultipartHttpServletRequest(HttpServletRequest request) {
		try {
			// 如果配置过了multipartResolver，就不需要手动调用resolveMultipart
			return (MultipartHttpServletRequest) request;
		} catch (Exception e) {
			/**
			 * what if we use JBoss web container, it will throw NullPointerException
			 * MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			 * MultipartHttpServletRequest mr = resolver.resolveMultipart(request);
			 */
			try {
				MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
				return resolver.resolveMultipart(request);
			} catch (Exception e2) {
				return null;
			}
		}
	}

}
