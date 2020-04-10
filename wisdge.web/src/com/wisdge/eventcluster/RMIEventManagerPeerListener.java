package com.wisdge.eventcluster;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisdge.eventcluster.multicast.Status;

public class RMIEventManagerPeerListener {
	private static final Log logger = LogFactory.getLog(RMIEventManagerPeerListener.class);

	private static final int MINIMUM_SENSIBLE_TIMEOUT = 200;
	private static final int NAMING_UNBIND_RETRY_INTERVAL = 400;
	private static final int NAMING_UNBIND_MAX_RETRIES = 10;

	/**
	 * The cache peers. The value is an RMIEventPeer.
	 */
	protected final Map<String, RMIEventPeer> eventPeers = new HashMap<String, RMIEventPeer>();

	private RMIEventPeer rmiEventPeer;
	private Status status;

	/**
	 * The RMI listener port
	 */
	protected Integer port;

	private Registry registry;
	private boolean registryCreated;
	private final String hostName;
	private Integer socketTimeoutMillis;
	private Integer remoteObjectPort;

	public RMIEventManagerPeerListener(String hostName, Integer port, Integer remoteObjectPort, Integer socketTimeoutMillis) throws UnknownHostException {
		status = Status.STATUS_UNINITIALISED;

		if (hostName != null && hostName.length() != 0) {
			this.hostName = hostName;
			if (hostName.equals("localhost")) {
				logger.warn("Explicitly setting the listener hostname to 'localhost' is not recommended. It will only work if all EventManager peers are on the same machine.");
			}
		} else {
			this.hostName = calculateHostAddress();
		}
		if (port == null || port.intValue() == 0) {
			assignFreePort(false);
		} else {
			this.port = port;
		}

		// by default is 0, which is ok.
		this.remoteObjectPort = remoteObjectPort;

		if (socketTimeoutMillis == null || socketTimeoutMillis.intValue() < MINIMUM_SENSIBLE_TIMEOUT) {
			throw new IllegalArgumentException("socketTimoutMillis must be a reasonable value greater than 200ms");
		}
		this.socketTimeoutMillis = socketTimeoutMillis;
	}

	/**
	 * Calculates the host address as the default NICs IP address
	 *
	 * @throws UnknownHostException
	 */
	protected String calculateHostAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * Assigns a free port to be the listener port.
	 *
	 * @throws IllegalStateException
	 *             if the statics of the listener is not {@Status#STATUS_UNINITIALISED}
	 */
	protected void assignFreePort(boolean forced) throws IllegalStateException {
		if (status != Status.STATUS_UNINITIALISED) {
			throw new IllegalStateException("Cannot change the port of an already started listener.");
		}
		this.port = Integer.valueOf(this.getFreePort());
		if (forced) {
			logger.warn("Resolving RMI port conflict by automatically using a free TCP/IP port to listen on: " + this.port);
		} else {
			logger.debug("Automatically finding a free TCP/IP port to listen on: " + this.port);
		}
	}

	/**
	 * Gets a free server socket port.
	 *
	 * @return a number in the range 1025 - 65536 that was free at the time this method was executed
	 * @throws IllegalArgumentException
	 */
	protected int getFreePort() throws IllegalArgumentException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
			return serverSocket.getLocalPort();
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not acquire a free port number.");
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
	
	public RMIEventPeer getEventPeer() {
		return rmiEventPeer;
	}

	public void init() throws Exception {
		if (!status.equals(Status.STATUS_UNINITIALISED)) {
			return;
		}

		rmiEventPeer = new RMIEventPeer(hostName, port, remoteObjectPort, socketTimeoutMillis);
		try {
			startRegistry();
			bind(rmiEventPeer.getUrl(), rmiEventPeer);
			logger.debug("RMICachePeers bound in registry for RMI listener");
			status = Status.STATUS_ALIVE;
		} catch (Exception e) {
			throw new Exception("Problem starting listener for RMIEventPeer " + rmiEventPeer.getUrl() + ". Initial cause was " + e.getMessage(), e);
		}
	}

	/**
	 * Bind a cache peer
	 *
	 * @param rmiEventPeer
	 */
	protected void bind(String peerName, RMIEventPeer rmiEventPeer) throws Exception {
		Naming.rebind(peerName, rmiEventPeer);
	}

	protected void startRegistry() throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry(port.intValue());
			try {
				registry.list();
			} catch (RemoteException e) {
				// may not be created. Let's create it.
				registry = LocateRegistry.createRegistry(port.intValue());
				registryCreated = true;
			}
		} catch (ExportException exception) {
			logger.error("Exception starting RMI registry. Error was " + exception.getMessage(), exception);
		}
	}

	protected void stopRegistry() throws RemoteException {
		if (registryCreated) {
			// the unexportObject call must be done on the Registry object returned
			// by createRegistry not by getRegistry, a NoSuchObjectException is
			// thrown otherwise
			boolean success = UnicastRemoteObject.unexportObject(registry, true);
			if (success) {
				logger.debug("rmiregistry unexported.");
			} else {
				logger.warn("Could not unexport rmiregistry.");
			}
		}
	}

	/**
	 * Returns a reference to the remote object.
	 *
	 * @param name
	 *            the name of the peer
	 */
	protected Remote lookupPeer(String name) throws Exception {
		try {
			return registry.lookup(name);
		} catch (Exception e) {
			throw new Exception("Unable to lookup peer for replicated cache " + name + " " + e.getMessage());
		}
	}

	public void dispose() throws Exception {
		if (!status.equals(Status.STATUS_ALIVE)) {
			return;
		}
		try {
			unbind(rmiEventPeer);
			stopRegistry();
			logger.debug("RMICachePeers unbound from registry in RMI listener");
			status = Status.STATUS_SHUTDOWN;
		} catch (Exception e) {
			throw new Exception("Problem unbinding remote cache peers. Initial cause was " + e.getMessage(), e);
		}
	}

	/**
	 * Unbinds an RMICachePeer and unexports it.
	 * <p/>
	 * We unbind from the registry first before unexporting. Unbinding first removes the very small possibility of a client getting the object from the registry
	 * while we are trying to unexport it.
	 * <p/>
	 * This method may take up to 4 seconds to complete, if we are having trouble unexporting the peer.
	 *
	 * @param rmiCachePeer
	 *            the bound and exported cache peer
	 * @throws Exception
	 */
	protected void unbind(RMIEventPeer rmiEventPeer) throws Exception {
		String url = rmiEventPeer.getUrl();
		try {
			Naming.unbind(url);
		} catch (NotBoundException e) {
			logger.warn(url + " not bound therefore not unbinding.");
		}
		// Try to gracefully unexport before forcing it.
		boolean unexported = UnicastRemoteObject.unexportObject(rmiEventPeer, false);
		for (int count = 1; (count < NAMING_UNBIND_MAX_RETRIES) && !unexported; count++) {
			try {
				Thread.sleep(NAMING_UNBIND_RETRY_INTERVAL);
			} catch (InterruptedException ie) {
				// break out of the unexportObject loop
				break;
			}
			unexported = UnicastRemoteObject.unexportObject(rmiEventPeer, false);
		}

		// If we still haven't been able to unexport, force the unexport
		// as a last resort.
		if (!unexported) {
			if (!UnicastRemoteObject.unexportObject(rmiEventPeer, true)) {
				logger.warn("Unable to unexport rmiCachePeer: " + rmiEventPeer.getUrl() + ".  Skipping.");
			}
		}
	}

}
