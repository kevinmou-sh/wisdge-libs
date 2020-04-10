package com.wisdge.web.servlets;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlobalSessionListener implements HttpSessionListener {
	private Log logger = LogFactory.getLog(GlobalSessionListener.class);
	private static List<HttpSession> onlinePerson;
	private static int visitors = 0;

	public GlobalSessionListener() {
		visitors = 0;
		onlinePerson = new ArrayList<HttpSession>();
	}

	public static List<HttpSession> getOnlinePerson() {
		return onlinePerson;
	}

	public static void setVisitors(int value) {
		visitors = value;
	}

	public static int getVisitors() {
		return visitors;
	}

	/**
	 * 根据ID查询对应的HttpSession
	 * 
	 * @param id
	 *            HttpSession的ID
	 * @return HttpSession对象
	 */
	public static HttpSession getSessionById(String id) {
		for (HttpSession session : onlinePerson) {
			if (session.getId().equals(id))
				return session;
		}
		return null;
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		onlinePerson.add(event.getSession());
		logger.debug("Created a new session[" + event.getSession().getId() + "], now has " + onlinePerson.size() + " onlines");
		visitors++;
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		onlinePerson.remove(event.getSession());
		logger.debug("Destoryed a session[" + event.getSession().getId() + "], now has " + onlinePerson.size() + " onlines");
	}

}
