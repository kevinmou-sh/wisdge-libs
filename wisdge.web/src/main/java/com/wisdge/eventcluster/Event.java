package com.wisdge.eventcluster;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Event implements Serializable {
	private String id;
	private Object value;
	private long timestamp;

	public Event() {
		this.timestamp = new Date().getTime();
	}

	public Event(String eventId, Object value) {
		this.id = eventId;
		this.value = value;
		this.timestamp = new Date().getTime();
	}
}
