package com.wisdge.eventcluster.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisdge.eventcluster.EventManager;
import com.wisdge.eventcluster.EventPeer;
import com.wisdge.eventcluster.EventPeerEntry;
import com.wisdge.eventcluster.RMIEventManagerPeerProvider;

/**
 * A peer provider which discovers peers using Multicast.
 * <p/>
 * Hosts can be in three different levels of conference with the Multicast specification (RFC1112), according to the requirements they meet.
 * <ol>
 * <li>Level 0 is the "no support for IP Multicasting" level. Lots of hosts and routers in the Internet are in this state, as multicast support is not mandatory
 * in IPv4 (it is, however, in IPv6). Not too much explanation is needed here: hosts in this level can neither send nor receive multicast packets. They must
 * ignore the ones sent by other multicast capable hosts.
 * <li>Level 1 is the "support for sending but not receiving multicast IP datagrams" level. Thus, note that it is not necessary to join a multicast group to be
 * able to send datagrams to it. Very few additions are needed in the IP module to make a "Level 0" host "Level 1-compliant".
 * <li>Level 2 is the "full support for IP multicasting" level. Level 2 hosts must be able to both send and receive multicast traffic. They must know the way to
 * join and leave multicast groups and to propagate this information to multicast routers. Thus, they must include an Internet Group Management Protocol (IGMP)
 * implementation in their TCP/IP stack.
 * </ol>
 * <p/>
 * The list of EventPeers is maintained via heartbeats. rmiUrls are looked up using RMI and converted to EventPeers on registration. On lookup any stale
 * references are removed.
 */
public final class MulticastRMIEventManagerPeerProvider extends RMIEventManagerPeerProvider {
	private static final Log logger = LogFactory.getLog(MulticastRMIEventManagerPeerProvider.class);
	/**
	 * One tenth of a second, in ms
	 */
	protected static final int SHORT_DELAY = 100;

	private final MulticastKeepaliveHeartbeatReceiver heartBeatReceiver;
	private final MulticastKeepaliveHeartbeatSender heartBeatSender;

	/**
	 * Creates and starts a multicast peer provider
	 *
	 * @param groupMulticastAddress
	 *            224.0.0.1 to 239.255.255.255 e.g. 230.0.0.1
	 * @param groupMulticastPort
	 *            1025 to 65536 e.g. 4446
	 * @param hostAddress
	 *            the address of the interface to use for sending and receiving multicast. May be null.
	 */
	public MulticastRMIEventManagerPeerProvider(EventManager eventManager, InetAddress groupMulticastAddress, Integer groupMulticastPort, Integer timeToLive,
			InetAddress hostAddress) {
		super(eventManager);

		heartBeatReceiver = new MulticastKeepaliveHeartbeatReceiver(this, groupMulticastAddress, groupMulticastPort, hostAddress);
		heartBeatSender = new MulticastKeepaliveHeartbeatSender(eventManager, groupMulticastAddress, groupMulticastPort, timeToLive, hostAddress);
	}

	public final void init() throws Exception {
		heartBeatReceiver.init();
		heartBeatSender.init();
	}

	/**
	 * Register a new peer, but only if the peer is new, otherwise the last seen timestamp is updated.
	 * <p/>
	 * This method is thread-safe. It relies on peerUrls being a synchronizedMap
	 *
	 * @param rmiUrl
	 */
	public final void registerPeer(String rmiUrl) {
		try {
			EventPeerEntry eventPeerEntry = (EventPeerEntry) peerUrls.get(rmiUrl);
			if (eventPeerEntry == null || stale(eventPeerEntry.getDate())) {
				// can take seconds if there is a problem
				EventPeer eventPeer = lookupRemoteEventPeer(rmiUrl);
				eventPeerEntry = new EventPeerEntry(eventPeer, new Date());
				// synchronized due to peerUrls being a synchronizedMap
				peerUrls.put(rmiUrl, eventPeerEntry);
			} else {
				eventPeerEntry.setDate(new Date());
			}
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				//logger.debug("Unable to lookup remote event peer for " + rmiUrl + ". Removing from peer list. Cause was: " + e.getMessage());
			}
			unregisterPeer(rmiUrl);
		} catch (NotBoundException e) {
			peerUrls.remove(rmiUrl);
			if (logger.isDebugEnabled()) {
				//logger.debug("Unable to lookup remote event peer for " + rmiUrl + ". Removing from peer list. Cause was: " + e.getMessage());
			}
		} catch (Throwable t) {
			logger.error("Unable to lookup remote event peer for " + rmiUrl
					+ ". Cause was not due to an IOException or NotBoundException which will occur in normal operation: " + t.getMessage());
		}
	}

	/**
	 * Shutdown the heartbeat
	 */
	public final void dispose() {
		heartBeatSender.dispose();
		heartBeatReceiver.dispose();
	}

	/**
	 * Time for a cluster to form. This varies considerably, depending on the implementation.
	 *
	 * @return the time in ms, for a cluster to form
	 */
	public long getTimeForClusterToForm() {
		return MulticastKeepaliveHeartbeatSender.getHeartBeatInterval() * 2 + SHORT_DELAY;
	}

	/**
	 * The time after which an unrefreshed peer provider entry is considered stale.
	 */
	protected long getStaleTime() {
		return MulticastKeepaliveHeartbeatSender.getHeartBeatStaleTime();
	}

	/**
	 * Whether the entry should be considered stale. This will depend on the type of RMICacheManagerPeerProvider. This method should be overridden for
	 * implementations that go stale based on date
	 *
	 * @param date
	 *            the date the entry was created
	 * @return true if stale
	 */
	protected final boolean stale(Date date) {
		long now = System.currentTimeMillis();
		return date.getTime() < (now - getStaleTime());
	}

	/**
	 * @return the MulticastKeepaliveHeartbeatReceiver
	 */
	public MulticastKeepaliveHeartbeatReceiver getHeartBeatReceiver() {
		return heartBeatReceiver;
	}

	/**
	 * @return the MulticastKeepaliveHeartbeatSender
	 */
	public MulticastKeepaliveHeartbeatSender getHeartBeatSender() {
		return heartBeatSender;
	}

	@Override
	public List<EventPeer> listRemoteEventPeers() throws Exception {
        List<EventPeer> remoteEventPeers = new ArrayList<EventPeer>();
        List<String> staleList = new ArrayList<String>();
        synchronized (peerUrls) {
            for (Iterator<String> iterator = peerUrls.keySet().iterator(); iterator.hasNext();) {
                String rmiUrl = (String) iterator.next();
                try {
                    EventPeerEntry eventPeerEntry = (EventPeerEntry) peerUrls.get(rmiUrl);
                    Date date = eventPeerEntry.getDate();
                    if (!stale(date)) {
                        EventPeer eventPeer = eventPeerEntry.getEventPeer();
                        remoteEventPeers.add(eventPeer);
                    } else {
                        logger.debug("rmiUrl is stale. Either the remote peer is shutdown or the network connectivity has been interrupted. Will be removed from list of remote event peers " + rmiUrl);
                        staleList.add(rmiUrl);
                    }
                } catch (Exception exception) {
                    logger.error(exception.getMessage(), exception);
                    throw new Exception("Unable to list remote event peers. Error was " + exception.getMessage());
                }
            }
            //Must remove entries after we have finished iterating over them
            for (int i = 0; i < staleList.size(); i++) {
                String rmiUrl = (String) staleList.get(i);
                peerUrls.remove(rmiUrl);
            }
        }
        return remoteEventPeers;
	}
}
