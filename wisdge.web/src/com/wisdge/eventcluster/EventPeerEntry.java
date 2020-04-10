package com.wisdge.eventcluster;

import java.util.Date;

public class EventPeerEntry {
	private final EventPeer eventPeer;
	private Date date;

	/**
	 * Constructor
	 *
	 * @param eventPeer
	 *            the cache peer part of this entry
	 * @param date
	 *            the date part of this entry
	 */
	public EventPeerEntry(EventPeer eventPeer, Date date) {
		this.eventPeer = eventPeer;
		this.date = date;
	}

	/**
	 * @return the event peer part of this entry
	 */
	public final EventPeer getEventPeer() {
		return eventPeer;
	}

	/**
	 * @return the date part of this entry
	 */
	public final Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
