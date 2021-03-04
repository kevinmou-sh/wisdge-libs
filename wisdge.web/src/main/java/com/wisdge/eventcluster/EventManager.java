package com.wisdge.eventcluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class EventManager implements Runnable {
	private static final Log logger = LogFactory.getLog(EventManager.class);
	
	private ExecutorService processingThreadPool;
	private Thread mainThread;
	private boolean stop = false;
	
	protected RMIEventPeer eventPeer;
	protected final List<EventListener> listeners = new ArrayList<EventListener>();
	
	protected RMIEventManagerPeerListener eventManagerPeerListener;
	protected RMIEventManagerPeerProvider eventManagerPeerProvider;

	public void init() {
		try {
			processingThreadPool = Executors.newCachedThreadPool(new NamedThreadFactory("Service cluster"));
			mainThread = new Thread(this);
			mainThread.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(! stop) {
			final Event event = eventPeer.pop();
			if (event == null)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			else {
				processingThreadPool.execute(new Runnable() {
					public void run() {
						for(EventListener listener: listeners) {
							listener.fireEvent(event);
						}
					}
				});
			}
		}
	}

	public void dispose() throws Exception {
		logger.debug("Dispose event manager, include peer listener and provider.");
		eventManagerPeerListener.dispose();
		eventManagerPeerProvider.dispose();
		stop = true;
		mainThread.interrupt();
	}

	/**
	 * 加入一个事件坚挺者
	 * 
	 * @param listener
	 *            EventListener对象
	 */
	public void addEventListener(EventListener listener) {
		logger.info("Add new event listener: " + listener.getName());
		listeners.add(listener);
	}

	/**
	 * 移除一个事件监听者
	 * 
	 * @param listener
	 */
	public void removeEventListener(EventListener listener) {
		listeners.remove(listener);
	}

	public List<EventListener> getListeners() {
		return listeners;
	}

	/**
	 * 向组群服务中广播一个消息事件
	 * @param eventId 消息对象ID
	 * @param eventValue 消息值
	 */
	public void addEvent(String eventId, Object eventValue) {
		this.addEvent(eventId, eventValue, false, null);
	}
	
	/**
	 * @param eventId 消息对象ID
	 * @param eventValue 消息值
	 * @param includeSelf boolean, 广播消息队列中是否需要包括本机 
	 */
	public void addEvent(String eventId, Object eventValue, boolean includeSelf) {
		this.addEvent(eventId, eventValue, includeSelf, null);
	}
	
	/**
	 * @param eventId 消息对象ID
	 * @param eventValue 消息值
	 * @param goalInstanceAddress boolean, 只向指定目标服务器广播
	 */
	public void addEvent(String eventId, Object eventValue, String goalInstanceAddress) {
		this.addEvent(eventId, eventValue, false, goalInstanceAddress);
	}
	
	/**
	 * 
	 * @param eventId 消息对象ID
	 * @param eventValue 消息值
	 * @param includeSelf 广播消息队列中是否需要包括本机 
	 * @param goalInstanceAddress 只向指定目标服务器广播
	 */
	public void addEvent(String eventId, Object eventValue, boolean includeSelf, String goalInstanceAddress) {
		if (includeSelf)
			eventManagerPeerListener.getEventPeer().addEvent(eventId, eventValue);

		try {
			for(EventPeer eventPeer : eventManagerPeerProvider.listRemoteEventPeers()) {
				if(goalInstanceAddress != null) {
					if(goalInstanceAddress.equals(eventPeer.getUrlBase()))
						eventPeer.addEvent(eventId, eventValue);
				}else
					eventPeer.addEvent(eventId, eventValue);
			}
		} catch (Exception e) {	
			logger.error(e, e);
		}
	}
	
	/**
	 * 获取当前集群的所有服务
	 * @param includeSelf
	 * @return List<String>
	 */
	public List<String> getPeers(boolean includeSelf) {
		List<String> peers = new ArrayList<String>();
		
		if (includeSelf)
			peers.add(eventManagerPeerListener.getEventPeer().getUrlBase());

		try {
			for(EventPeer eventPeer : eventManagerPeerProvider.listRemoteEventPeers()) {
				peers.add(eventPeer.getUrlBase());
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return peers;
	}

	public RMIEventManagerPeerListener getEventManagerPeerListener() {
		return this.eventManagerPeerListener;
	}
	
	public RMIEventManagerPeerProvider getEventManagerPeerProvider() {
		return this.eventManagerPeerProvider;
	}
}
