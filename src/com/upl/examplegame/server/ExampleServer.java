package com.upl.examplegame.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.Server;
import com.upl.mmorpg.lib.libnet.ServerListener;

public class ExampleServer implements ServerListener
{
	public ExampleServer()
	{
		server = new Server(this, 8080);
		clients = new LinkedList<ClientHandler>();
	}
	
	@Override
	public void acceptClient(Socket socket, int cid) 
	{
		try 
		{
			ClientHandler handler = new ClientHandler(this, socket, cid);
			clients.add(handler);
		} catch (IOException e) 
		{
			Log.wtf("Client could not be created!", e);
		}
	}
	
	public void broadcast(String sender, String message)
	{
		Iterator<ClientHandler> it = clients.iterator();
		while(it.hasNext())
		{
			ClientHandler handle = it.next();
			handle.send_message(sender, message);
		}
	}
	
	public void disconnected(ClientHandler client, Socket sock)
	{
		server.disconnected(sock);
		clients.remove(client);
	}
	
	private LinkedList<ClientHandler> clients;
	
	private Server server;
	
	public static void main(String[] args) 
	{
		new ExampleServer();
	}
}
