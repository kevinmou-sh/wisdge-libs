package com.wisdge.web.filters.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class SavedRequestManager {
	private static final String SAVED_REQUESTS_IDENTIFIER = "srid";
	private static final String SAVED_REQUESTS_KEY = SavedRequest.class.getCanonicalName() + "_SavedRequests";

	public static String getSavedRequestIdentifier() {
		return SAVED_REQUESTS_IDENTIFIER;
	}

	public static String getSavedRequestKey() {
		return SAVED_REQUESTS_KEY;
	}

	public static String saveRequest(HttpServletRequest request) {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Map<String, SavedRequest> savedRequests = (Map<String, SavedRequest>) session.getAttribute(getSavedRequestKey());

		if (savedRequests == null) {
			savedRequests = new HashMap<String, SavedRequest>();
			session.setAttribute(getSavedRequestKey(), savedRequests);
		}

		SavedRequest savedRequest = new SavedRequest(request);
		String key;
		do
			key = RandomStringUtils.randomAlphanumeric(8);
		while (savedRequests.containsKey(key));

		savedRequests.put(key, savedRequest);
		return key;
	}

	public static String getOriginalUrl(HttpServletRequest request) {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Map<String, SavedRequest> savedRequests = (Map<String, SavedRequest>) session.getAttribute(getSavedRequestKey());

		if (savedRequests != null) {
			String identifier = request.getParameter(getSavedRequestIdentifier());
			if (!StringUtils.isEmpty(identifier)) {
				SavedRequest savedRequest = (SavedRequest) savedRequests.get(request.getParameter(getSavedRequestIdentifier()));
				if (savedRequest != null) {
					return savedRequest.getRequestUrl() + "?srid=" + identifier;
				}
			}
		}
		return null;
	}

	public static class SavedRequest implements Serializable {
		private static final long serialVersionUID = 8779129900717599986L;
		private Map<String, String[]> parameters;
		private String requestUrl;

		public SavedRequest(HttpServletRequest request) {
			this.parameters = new HashMap<String, String[]>(request.getParameterMap());
			this.requestUrl = request.getRequestURL().toString();
		}

		public String getParameter(String name) {
			String[] values = (String[]) this.parameters.get(name);
			if ((values != null) && (values.length > 0)) {
				return values[0];
			}
			return null;
		}

		public String[] getParameterValues(String name) {
			return (String[]) this.parameters.get(name);
		}

		public Map<String, String[]> getParameterMap() {
			return this.parameters;
		}

		public String getRequestUrl() {
			return this.requestUrl;
		}
	}
}