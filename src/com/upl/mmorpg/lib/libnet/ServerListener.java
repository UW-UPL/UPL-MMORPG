package com.upl.mmorpg.lib.libnet;

import java.net.Socket;

public interface ServerListener 
{
	/**
	 * Called when a client connects to the server.
	 * @param socket The socket of the client that connected.
	 */
	public void acceptClient(Socket socket, int cid);
}
