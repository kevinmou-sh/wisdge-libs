package com.wisdge.eventcluster.manual;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisdge.eventcluster.EventPeer;
import com.wisdge.eventcluster.RMIEventManagerPeerProvider;

public class ManualRMIEventManagerPeerProvider extends RMIEventManagerPeerProvider {
	private static final Log logger = LogFactory.getLog(ManualRMIEventManagerPeerProvider.class);
	private List<String> peers;
	private boolean trace;

	public ManualRMIEventManagerPeerProvider(List<String> peers, boolean trace) {
		this.peers = peers;
		this.trace = trace;
	}

	@Override
	public void init() throws Exception {
		for(String rmiUrl : peers) {
			logger.debug("Registering peer " + rmiUrl);
		}
	}

	@Override
	public void registerPeer(String rmiUrl) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean stale(Date date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<EventPeer> listRemoteEventPeers() throws Exception {
		List<EventPeer> remoteEventPeers = new ArrayList<EventPeer>();
		for (String rmiUrl : peers) {
			try {
				if (! rmiUrl.toLowerCase().startsWith("rmi://"))
					rmiUrl = "rmi://" + rmiUrl + "/event";
				EventPeer eventPeer = lookupRemoteEventPeer(rmiUrl);
				remoteEventPeers.add(eventPeer);
			} catch (Exception e) {
				// make it silent
				if (trace) {
					logger.debug("Looking up rmiUrl " + rmiUrl + " through exception " + e.getMessage()
							+ ". This may be normal if a node has gone offline. Or it may indicate network connectivity" + " difficulties", e);
				}
			}
		}

		return remoteEventPeers;
	}

}
