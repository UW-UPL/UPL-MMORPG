package com.upl.examplegame.server;

import java.io.IOException;
import java.net.Socket;

import com.upl.mmorpg.lib.librpc.RPCListener;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class ClientHandler implements RPCListener
{
	public ClientHandler(ExampleServer server, Socket socket, int cid) 
			throws IOException
	{
		rpc = new RPCManager(this, socket, cid, new RPCCalleeStubs(this));
		caller = new RPCCallerStubs(rpc);
		this.server = server;
		this.socket = socket;
		sender = null;
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
	
	public boolean echo(String message)
	{
		System.out.println("Received echo: " + message);
		return true;
	}
	
	public boolean send_message(String sender, String message)
	{
		caller.receive_message(sender, message);
		return true;
	}
	
	public void shutdown()
	{
		socket = null;
		server = null;
		rpc = null;
		caller = null;
	}
	
	@Override
	public void connectionLost() 
	{
		server.disconnected(this, socket);
		shutdown();
	}
	
	private Socket socket;
	private String sender;
	private ExampleServer server;
	private RPCManager rpc;
	private RPCCallerStubs caller;
}
