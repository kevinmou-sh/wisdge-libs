package com.wisdge.eventcluster;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;
	private final Object value;
	private final long timestamp;
	
	public Event(String eventId, Object value) {
		this.id = eventId;
		this.value = value;
		this.timestamp = new Date().getTime();
	}

	public String getId() {
		return id;
	}

	public Object getValue() {
		return value;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
