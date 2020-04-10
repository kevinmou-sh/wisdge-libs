package com.wisdge.eventcluster;

public interface EventListener {
	
	public String getName();
	
	/**
	 * @param event Event消息对象
	 */
	public void fireEvent(Event event);
}
