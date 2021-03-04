package com.wisdge.eventcluster;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisdge.eventcluster.multicast.MulticastRMIEventManagerPeerProvider;
import com.wisdge.utils.StringUtils;

public class MulticastEventManager extends EventManager {
	private static final Log logger = LogFactory.getLog(MulticastEventManager.class);
	private static final int MAXIMUM_TTL = 255;

	private String hostName;
	private Integer multicastPort;
	private String multicastGroupAddress;
	private String portRange;
	private Integer timeToLive;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getMulticastPort() {
		return multicastPort;
	}

	public void setMulticastPort(Integer multicastPort) {
		this.multicastPort = multicastPort;
	}

	public String getMulticastGroupAddress() {
		return multicastGroupAddress;
	}

	public void setMulticastGroupAddress(String multicastGroupAddress) {
		this.multicastGroupAddress = multicastGroupAddress;
	}

	public Integer getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	public String getPortRange() {
		return portRange;
	}

	public void setPortRange(String portRange) {
		this.portRange = portRange;
	}

	@Override
	public void init() {
		try {
			if (StringUtils.isEmpty(hostName))
				hostName = calculateHostAddress();
			
			if (StringUtils.isEmpty(portRange)) {
				eventManagerPeerListener = new RMIEventManagerPeerListener(hostName, null, 0, 2000);
			} else {
				int port = getFreePortWithRange(portRange);
				eventManagerPeerListener = new RMIEventManagerPeerListener(hostName, port, 0, 2000);
			}
			eventManagerPeerListener.init();
			eventPeer = eventManagerPeerListener.getEventPeer();

			eventManagerPeerProvider = createAutomaticallyConfiguredCachePeerProvider();
			eventManagerPeerProvider.init();
			logger.debug("Multicast event manager start at: " + multicastGroupAddress + ", port:" + multicastPort);
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

	/**
	 * peerDiscovery=automatic, multicastGroupAddress=230.0.0.1, multicastGroupPort=4447, multicastPacketTimeToLive=255
	 */
	protected RMIEventManagerPeerProvider createAutomaticallyConfiguredCachePeerProvider() throws Exception {
		InetAddress hostAddress = null;
		hostAddress = InetAddress.getByName(hostName);

		InetAddress groupAddress = InetAddress.getByName(multicastGroupAddress);
		if (timeToLive == null) {
			timeToLive = Integer.valueOf(1);
			logger.debug("No TTL set. Setting it to the default of 1, which means packets are limited to the same subnet.");
		} else {
			if (timeToLive.intValue() < 0 || timeToLive.intValue() > MAXIMUM_TTL) {
				throw new Exception("The TTL must be set to a value between 0 and 255");
			}
		}
		return new MulticastRMIEventManagerPeerProvider(this, groupAddress, multicastPort, timeToLive, hostAddress);
	}

	public static void main(String[] args) throws Exception {
		MulticastEventManager eventManager = new MulticastEventManager();
		eventManager.setMulticastGroupAddress("230.0.0.1");
		eventManager.setMulticastPort(4404);
		eventManager.setPortRange("5501-5510");
		eventManager.setTimeToLive(120);
		eventManager.init();
		eventManager.addEventListener(new EventListener() {
			@Override
			public void fireEvent(Event event) {
				System.out.println("event: " + event.getId() + " - " + event.getValue());
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "qc1";
			}
		});

		while(true) {
			Thread.sleep(5000);
			System.out.println(eventManager.getPeers(false));
			eventManager.addEvent("111", "bbb");
		}
	}

}
