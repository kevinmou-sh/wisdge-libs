package com.wisdge.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LiveMap<K, V> {
	private Map<K, TTLBean<V>> map = new HashMap<K, TTLBean<V>>();
	private long timeToLive;
	private Timer timer;

	public Map<K, V> getMap() {
		Map<K, V> collection = new HashMap<K, V>();
		synchronized (map) {
			Iterator<K> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				K key = iter.next();
				collection.put(key, map.get(key).getBean());
			}
			return collection;
		}
	}

	public void put(K key, V value) {
		map.put(key, new TTLBean<V>(value));
	}

	public V get(K key) {
		TTLBean<V> bean = map.get(key);
		return bean == null ? null : bean.getBean();
	}

	public void update(K key) {
		TTLBean<V> bean = map.get(key);
		if (bean != null)
			bean.setDate(new Date());
	}

	private void ttl() {
		long now = new Date().getTime();
		synchronized (map) {
			Iterator<K> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				K key = iter.next();
				if (now - map.get(key).getDate().getTime() > timeToLive)
					map.remove(key);
			}
		}
	}

	public LiveMap(long timeToLive) {
		this(timeToLive, 60000);
	}

	public LiveMap(long timeToLive, long period) {
		this.timeToLive = timeToLive;
		this.timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ttl();
			}
		}, period, period);
	}

	public void destroy() {
		timer.cancel();
	}
}

class TTLBean<T> {
	private Date date;
	private T bean;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public T getBean() {
		return bean;
	}

	public void setBean(T bean) {
		this.bean = bean;
	}

	public TTLBean(T bean) {
		this.bean = bean;
		this.date = new Date();
	}
}
