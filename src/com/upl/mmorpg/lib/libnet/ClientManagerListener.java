package com.upl.mmorpg.lib.libnet;

public interface ClientManagerListener 
{
	public void bytesReceived(byte bytes[]);
	public void connectionLost();
}
