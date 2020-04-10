package com.wisdge.web.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

import com.wisdge.web.filters.internal.SavedRequestManager;
import com.wisdge.web.filters.internal.SavedRequestManager.SavedRequest;

public class SavedRequestRestorerFilter implements Filter {
	private static final Pattern SAVED_REQUEST_REGEXP = Pattern.compile("(?:^|&)" + SavedRequestManager.getSavedRequestIdentifier() + "=([^&]++)");

	private static final String ATTRIBUTE_APPLIED = SavedRequestRestorerFilter.class.getName() + ".applied";

	public void init(FilterConfig filterConfig) {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ServletRequest filteredRequest = request;

		if (((request instanceof HttpServletRequest)) && (!Boolean.valueOf((String) request.getAttribute(ATTRIBUTE_APPLIED)).booleanValue())) {
			SavedRequestManager.SavedRequest savedRequest = getSavedRequest((HttpServletRequest) request);

			filteredRequest = new SavedRequestWrapper((HttpServletRequest) request, savedRequest);
			filteredRequest.setAttribute(ATTRIBUTE_APPLIED, "true");
		}

		chain.doFilter(filteredRequest, response);

		filteredRequest.removeAttribute(ATTRIBUTE_APPLIED);
	}

	public void destroy() {
	}

	protected SavedRequestManager.SavedRequest getSavedRequest(HttpServletRequest request) {
		String savedRequestId = null;

		Matcher m = SAVED_REQUEST_REGEXP.matcher(StringUtils.defaultString(request.getQueryString()));
		if (m.find()) {
			savedRequestId = m.group(1);
		}

		if (!StringUtils.isEmpty(savedRequestId)) {
			HttpSession session = request.getSession();

			Map<?, ?> savedRequests = (Map<?, ?>) session.getAttribute(SavedRequestManager.getSavedRequestKey());
			if (savedRequests != null) {
				SavedRequestManager.SavedRequest savedRequest = (SavedRequestManager.SavedRequest) savedRequests.get(savedRequestId);

				if ((savedRequest != null) && (StringUtils.equals(savedRequest.getRequestUrl(), request.getRequestURL().toString()))) {
					savedRequests.remove(savedRequestId);

					return savedRequest;
				}
			}
		}
		return null;
	}

	public static class SavedRequestWrapper extends HttpServletRequestWrapper {
		private SavedRequestManager.SavedRequest savedRequest;

		public SavedRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		public SavedRequestWrapper(HttpServletRequest request, SavedRequest savedRequest) {
			super(request);
			this.savedRequest = savedRequest;
		}

		public String getParameter(String name) {
			String value = super.getParameter(name);
			if ((value == null) && (this.savedRequest != null)) {
				value = this.savedRequest.getParameter(name);
			}
			return value;
		}

		public String[] getParameterValues(String name) {
			String[] values = super.getParameterValues(name);
			if ((values == null) && (this.savedRequest != null)) {
				values = this.savedRequest.getParameterValues(name);
			}
			return values;
		}

		public Map<String, String[]> getParameterMap() {
			if (this.savedRequest == null) {
				return super.getParameterMap();
			}

			Map<String, String[]> map = new HashMap<String, String[]>(this.savedRequest.getParameterMap());
			map.putAll(super.getParameterMap());
			return Collections.unmodifiableMap(map);
		}

		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(getParameterMap().keySet());
		}
	}
}