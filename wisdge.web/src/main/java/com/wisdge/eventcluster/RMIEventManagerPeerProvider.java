package com.wisdge.eventcluster;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A provider of Peer RMI addresses.
 */
public abstract class RMIEventManagerPeerProvider {

    /**
     * Contains a RMI URLs of the form: "//" + hostName + ":" + port + "/" + eventName;
     */
    protected final Map<String, EventPeerEntry> peerUrls = Collections.synchronizedMap(new HashMap<String, EventPeerEntry>());

    public Map<String, EventPeerEntry> getPeerUrls() {
		return peerUrls;
	}

	protected EventManager eventManager;

    /**
     * Constructor
     *
     * @param cacheManager
     */
    public RMIEventManagerPeerProvider(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Empty constructor
     */
    public RMIEventManagerPeerProvider() {
        //nothing to do
    }

	public abstract void init() throws Exception;

    /**
     * Register a new peer
     *
     * @param rmiUrl
     */
    public abstract void registerPeer(String rmiUrl);

    /**
     * Unregisters a peer
     *
     * @param rmiUrl
     */
    public synchronized void unregisterPeer(String rmiUrl) {
        peerUrls.remove(rmiUrl);
    }
    
    /**
     * Whether the entry should be considered stale. This will depend on the type of RMIEventManagerPeerProvider.
     * <p/>
     * @param date the date the entry was created
     * @return true if stale
     */
    protected abstract boolean stale(Date date);


    /**
     * The use of one-time registry creation and Naming.rebind should mean we can create as many listeneres as we like.
     * They will simply replace the ones that were there.
     */
    public EventPeer lookupRemoteEventPeer(String url) throws MalformedURLException, NotBoundException, RemoteException {
        // logger.debug("Lookup URL " + url);
        EventPeer eventPeer = (EventPeer) Naming.lookup(url);
        return eventPeer;
    }
    
    /**
     * @return a list of {@link net.sf.ehcache.distribution.CachePeer} peers for the given cache, excluding the local peer.
     */
    public abstract List<EventPeer> listRemoteEventPeers() throws Exception;
    
    /**
     * Providers may be doing all sorts of exotic things and need to be able to clean up on dispose.
     *
     * @throws net.sf.ehcache.CacheException
     */
    public void dispose() throws Exception {
        //nothing to do.
    }

    /**
     * The eventManager this provider is bound to
     */
    public final EventManager getEventManager() {
        return eventManager;
    }
}
