package com.upl.mmorpg.lib.libnet;

import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;;

public class Server implements Runnable 
{
	public Server(ServerListener listener)
	{
		this.listener = listener;
		this.port = DEFAULT_PORT;
		clients = new LinkedList<Socket>();
	}
	
	public Server(ServerListener listener, int port)
	{
		this.listener = listener;
		this.port = port;
		clients = new LinkedList<Socket>();
	}
	
	public boolean startServer()
	{
		if(running) return true;
		
		clientCounter = 1;
		
		try
		{
			/* Open the port for the RPC server */
			Log.v("Opening port...\t\t\t\t\t");
			serverSocket = new ServerSocket(port);
			Log.vok();
			
			Log.v("Starting listen thread...\t\t\t");
			running = true;
			listenThread = new Thread(this);
			listenThread.start();
			Log.vok();
		}catch (Exception e)
		{
			Log.vfail();
			Log.wtf("Port Busy", e);
			return false;
		}
		
		return true;
	}
	
	@Override
	public void run()
	{
		Log.vln("Ready for clients...");
		while(running)
		{
			try
			{
				/* Accept a client socket */
				Socket client = serverSocket.accept();
				clients.add(client);
				Log.vln("Received client " + clientCounter);
				
				listener.acceptClient(client, clientCounter++);
				/* Were we interrupted due to a shutdown? */
				if(!running) break;
			} catch(Exception e)
			{
				Log.wtf("Failed to accept client!", e);
			}
		}
	}
	
	/**
	 * Notify the server that a client has lost connection
	 * @param socket The socket the client was connected to
	 */
	public void disconnected(Socket socket)
	{
		clients.remove(socket);
	}
	
	/**
	 * Shutdown the server and disconnect all clients
	 */
	public void shutdown()
	{
		Iterator<Socket> it = clients.iterator();
		while(it.hasNext())
		{
			Socket client = it.next();
			try { client.close(); } catch (Exception e){};
			client = null;
			it.remove();
		}
		
		/* Close the server socket */
		try { serverSocket.close(); } catch(Exception e){}
		running = false;
		
		/* Interrupt thread if it is waiting */
		try
		{
			listenThread.interrupt();
		}catch(Exception e){}
		
		/* Wait for the thread to die (one second max) */
		try
		{
			listenThread.join(1000);
		} catch(Exception e){}
		
		serverSocket = null;
		listenThread = null;
	}
	
	private int port; /* The port the server is running on */
	private int clientCounter; /* The amount of clients that have connected. */
	private LinkedList<Socket> clients; /* Connected sockets */
	private ServerListener listener; /* The object waiting for clients to accept */
	
	private ServerSocket serverSocket; /* The socket used to accept clients */
	private Thread listenThread; /* The thread that is used to listen for clients. */
	private boolean running;
	
	/** If a port is not supplied, the default port will be used. */
	private final static int DEFAULT_PORT = 8081;
}
