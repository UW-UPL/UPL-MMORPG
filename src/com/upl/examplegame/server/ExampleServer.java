package com.upl.examplegame.server;

import com.upl.mmorpg.lib.libnet.Server;

public class ExampleServer 
{
	public ExampleServer()
	{
		server = new Server(8080);
	}
	
	public void broadcast(String sender, String message)
	{
		
	}
	
	private Server server;
	
	public static void main(String[] args) 
	{
		ExampleServer server = new ExampleServer();
	}
}
