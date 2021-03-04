package com.wisdge.eventcluster;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EventPeer extends Remote {
	String getUrl() throws RemoteException;
	String getUrlBase() throws RemoteException;
	void addEvent(String eventId, Object eventValue) throws RemoteException;
}
