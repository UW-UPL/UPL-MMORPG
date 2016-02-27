package com.upl.examplegame.server;

import java.net.Socket;

import com.upl.mmorpg.lib.librpc.RPCManager;

public class ClientHandler 
{
	public ClientHandler(ExampleServer server, Socket socket, int cid)
	{
		calleeStubs = new RPCCalleeStubs(this);
		serverrpc = new RPCManager(socket, cid, calleeStubs);
		
		this.server = server;
	}
	
	public boolean broadcast_message(String message)
	{
		if(sender == null)
			return false;
		
		server.broadcast(sender, message);
		return true;
	}
	
	public boolean register(String player)
	{
		if(sender == null)
		{
			sender = player;
			return true;
		}
		return false;
	}
	
	private String sender;
	private ExampleServer server;
	private RPCCalleeStubs calleeStubs;
	private RPCManager serverrpc;
}
