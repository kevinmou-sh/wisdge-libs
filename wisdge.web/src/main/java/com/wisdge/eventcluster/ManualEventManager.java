package com.wisdge.eventcluster;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisdge.eventcluster.manual.ManualRMIEventManagerPeerProvider;
import com.wisdge.utils.StringUtils;

public class ManualEventManager extends EventManager {
	private static final Log logger = LogFactory.getLog(ManualEventManager.class);

	private String hostName;
	private Integer port;
	private String portRange;
	private List<String> peers;
	private boolean trace;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPortRange() {
		return portRange;
	}

	public void setPortRange(String portRange) {
		this.portRange = portRange;
	}

	public List<String> getPeers() {
		return peers;
	}

	public void setPeers(List<String> peers) {
		this.peers = peers;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public void init() {
		try {
			if (StringUtils.isEmpty(hostName))
				hostName = calculateHostAddress();
			
			if (StringUtils.isEmpty(portRange)) {
				eventManagerPeerListener = new RMIEventManagerPeerListener(hostName, port, 0, 2000);
			} else {
				port = getFreePortWithRange(portRange);
				eventManagerPeerListener = new RMIEventManagerPeerListener(hostName, port, 0, 2000);
			}
			eventManagerPeerListener.init();
			eventPeer = eventManagerPeerListener.getEventPeer();

			eventManagerPeerProvider = new ManualRMIEventManagerPeerProvider(peers, trace);
			eventManagerPeerProvider.init();
			logger.debug("RMI event manager start at: " + hostName + ", port:" + port);
		} catch (Exception e) {
			logger.error(e, e);
		}
		super.init();
	}

	/**
	 * Calculates the host address as the default NICs IP address
	 *
	 * @throws UnknownHostException
	 */
	private String calculateHostAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * Gets a free server socket port.
	 *
	 * @return a number in the range 1025 - 65536 that was free at the time this method was executed
	 * @throws IllegalArgumentException
	 */
	private int getFreePortWithRange(String portRange) throws IllegalArgumentException {
		int portStart = 1025, portEnd = 65534;
		String[] range = portRange.split("-");
		portStart = Integer.parseInt(range[0]);
		portEnd = Integer.parseInt(range[1]);
		
		ServerSocket serverSocket = null;
		for(int i=portStart; i<=portEnd; i++) {
			try {
				serverSocket = new ServerSocket(i);
				return serverSocket.getLocalPort();
			} catch (IOException e) {
				continue;
			} finally {
				if (serverSocket != null && !serverSocket.isClosed()) {
					try {
						serverSocket.close();
					} catch (Exception e) {
						logger.debug("Error closing ServerSocket: " + e.getMessage());
					}
				}
			}
		}
		throw new IllegalArgumentException("Could not acquire a free port number.");
	}

	public static void main(String[] args) throws Exception {
		ManualEventManager eventManager = new ManualEventManager();
		eventManager.setHostName("127.0.0.1");
		eventManager.setPortRange("5001-5005");
		List<String> peers = new ArrayList<String>();
		peers.add("127.0.0.1:5002");
		eventManager.setPeers(peers);
		eventManager.init();
		eventManager.addEventListener(new EventListener() {
			@Override
			public void fireEvent(Event event) {
				System.out.println("5001: " + event.getId() + " => " + event.getValue());
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "handleA";
			}
		});

		ManualEventManager eventManager2 = new ManualEventManager();
		eventManager2.setHostName("127.0.0.1");
		eventManager2.setPortRange("5001-5005");
		List<String> peers2 = new ArrayList<String>();
		peers2.add("127.0.0.1:5001");
		peers2.add("127.0.0.1:5003");
		eventManager2.setPeers(peers2);
		eventManager2.init();
		eventManager2.addEventListener(new EventListener() {
			@Override
			public void fireEvent(Event event) {
				System.out.println("5002: " + event.getId() + " => " + event.getValue());
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "handleB";
			}
		});

		while(true) {
			Thread.sleep(5000);
			System.out.println(eventManager.getPeers(true));
			eventManager.addEvent("111", "aaa");
			eventManager2.addEvent("222", "bbb");
		}
	}

}
