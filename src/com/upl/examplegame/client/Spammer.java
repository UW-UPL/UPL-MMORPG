package com.upl.examplegame.client;

import java.io.IOException;

import com.upl.mmorpg.lib.librpc.RPCTimeoutException;

public class Spammer extends ExampleClient
{
	public Spammer() throws IOException {
		super();
		
		try {
			Thread.sleep(3000);
			testing = false;
		} catch (InterruptedException e) {
		}
		
		rpc.shutdown();
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean testing;
	public void run()
	{
		testing = true;
		for(int x = 0;testing;x++)
		{
			final int num = x;
			Runnable run = new Runnable()
			{
				public void run()
				{
					try
					{
						call.echo("Message num " + (num + 1));
					} catch(RPCTimeoutException e){}
				}
			};
			new Thread(run).start();
		}
	}
	
	public static void main(String args[])
	{
		try {
			new Spammer();
		} catch (IOException e) {
		}
	}
}
