package com.upl.examplegame.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCListener;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class ExampleClient implements RPCListener
{
	public ExampleClient() throws IOException
	{
		try 
		{
			rpc = new RPCManager(this, "localhost", 8080, new RPCCalleeStubs(this));
			call = new RPCCallerStubs(rpc);
		} catch (IOException e) {
			Log.e("Could not connect to host!");
			return;
		}
		
		read = new BufferedReader(new InputStreamReader(System.in));
		
		/* Get the username from the player */
		System.out.print("Prefered username? ");
		String username = read.readLine();
		if(call.register(username))
			Log.vln("Username was available");
		else Log.e("Username was not available!");
		
		Runnable run = new Runnable()
		{
			public void run()
			{
				ExampleClient.this.run();
			}
		};
		new Thread(run).start();
	}
	
	public boolean receive_message(String sender, String message)
	{
		System.out.println(sender + " sent message: " + message);
		return true;
	}
	
	public void run()
	{
		try {
			while(true)
			{
				String message = read.readLine();
				Log.vln("Got message: " + message);
				if(message.equalsIgnoreCase("shutdown"))
				{
					rpc.shutdown();
					rpc = null;
					break;
				}else call.broadcast_message(message);
			}
		} catch (IOException e) {Log.wtf("Read message failure!", e);}
		
		if(rpc != null)
			rpc.shutdown();
	}
	
	public RPCManager rpc;
	public RPCCallerStubs call;
	private BufferedReader read;

	@Override
	public void connectionLost() {}
	
	public static void main(String args[])
	{
		try {
			new ExampleClient();
		} catch (IOException e) {
		}
	}
}
