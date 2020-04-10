package com.wisdge.eventcluster;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import com.wisdge.eventcluster.multicast.ConfigurableRMIClientSocketFactory;

public class RMIEventPeer extends UnicastRemoteObject implements EventPeer {
	private static final long serialVersionUID = -8097321424924393692L;
	private final String hostname;
	private final Integer rmiRegistryPort;
	private Integer remoteObjectPort;
	private final List<Event> events;

	/**
	 * Construct a new remote peer.
	 *
	 * @param hostName
	 *            The host name the peer is running on.
	 * @param rmiRegistryPort
	 *            The port number on which the RMI Registry listens. Should be an unused port in the range 1025 - 65536
	 * @param remoteObjectPort
	 *            the port number on which the remote objects bound in the registry receive calls. This defaults to a free port if not specified. Should be an
	 *            unused port in the range 1025 - 65536
	 * @param socketTimeoutMillis
	 * @throws RemoteException
	 */
	public RMIEventPeer(String hostName, Integer rmiRegistryPort, Integer remoteObjectPort, Integer socketTimeoutMillis) throws RemoteException {
		super(remoteObjectPort.intValue(), new ConfigurableRMIClientSocketFactory(socketTimeoutMillis), ConfigurableRMIClientSocketFactory
				.getConfiguredRMISocketFactory());

		this.remoteObjectPort = remoteObjectPort;
		this.hostname = hostName;
		this.rmiRegistryPort = rmiRegistryPort;
		this.events = new ArrayList<Event>();
	}

	public Integer getRemoteObjectPort() {
		return remoteObjectPort;
	}

	public void setRemoteObjectPort(Integer remoteObjectPort) {
		this.remoteObjectPort = remoteObjectPort;
	}

	public String getHostname() {
		return hostname;
	}

	public Integer getRmiRegistryPort() {
		return rmiRegistryPort;
	}
	
    /**
     * This implementation gives an URL which has meaning to the RMI remoting system.
     *
     * @return the URL, without the scheme, as a string e.g. //hostname:port/peerName
     */
    public final String getUrl() {
        return new StringBuilder()
                .append("rmi://")
                .append(hostname)
                .append(":")
                .append(rmiRegistryPort)
                .append("/")
                .append("event")
                .toString();
    }

    /**
     * This implementation gives an URL which has meaning to the RMI remoting system.
     *
     * @return the URL, without the scheme, as a string e.g. //hostname:port
     */
    public final String getUrlBase() {
        return new StringBuilder()
                .append(hostname)
                .append(":")
                .append(rmiRegistryPort)
                .toString();
    }

    /**
     * Returns a String that represents the value of this object.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder("URL: ");
        buffer.append(getUrl());
        buffer.append(" Remote Object Port: ");
        buffer.append(remoteObjectPort);
        return buffer.toString();
    }

	@Override
	public void addEvent(String eventId, Object eventValue) {
		this.addEvent(new Event(eventId, eventValue));
	}

	public void addEvent(Event event) {
		synchronized (events) {
			events.add(event);
		}
	}

	public Event pop() {
		synchronized (events) {
			if (events.size() > 0)
				return events.remove(0);
			else
				return null;
		}
	}
}
