package com.upl.mmorpg.lib.libnet;

import java.net.*;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;;

public class Server implements Runnable 
{
	private boolean setup(int port)
	{
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
	
	public Server()
	{
		if(setup(DEFAULT_PORT))
			shutdown();
	}
	
	public Server(int port)
	{
		if(!setup(port))
			shutdown();
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				Log.vln("Ready for clients...");
				/* Accept a client socket */
				Socket client = serverSocket.accept();
				Log.vln("Received client " + clientCounter);
				
				/* Were we interrupted due to a shutdown? */
				if(!running) break;
			} catch(Exception e){}
		}
	}
	
	public void shutdown()
	{
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
	
	private int clientCounter;
	private LinkedList<ClientManager> clients;
	
	private ServerSocket serverSocket;
	private Thread listenThread;
	private boolean running;
	
	private final static int DEFAULT_PORT = 8081;
}
