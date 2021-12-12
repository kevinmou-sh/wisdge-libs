package com.wisdge.eventcluster;

public interface EventListener {

	String getName();

	/**
	 * @param event Event消息对象
	 */
	void fireEvent(Event event);
}
