package com.upl.mmorpg.lib.libnet;

public interface NetworkListener 
{
	/**
	 * Called when a packet is received by the client/server.
	 * @param bytes The bytes received.
	 */
	public void bytesReceived(byte bytes[]);
	
	/**
	 * Called when the connection to the server/client
	 * is lost.
	 */
	public void connectionLost();
}
