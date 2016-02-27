package com.upl.mmorpg.lib;

import com.upl.mmorpg.lib.libnet.TicketManager;

public class TestMain 
{
	public static void main(String args[])
	{
		TestMain m = new TestMain();
		m.run_test();
	}

	public void run_test()
	{
		tickets = new TicketManager();
		Server s = new Server();
		new Thread(new Client(s)).start();
		new Thread(new Client(s)).start();
		new Thread(new Client(s)).start();
		new Thread(new Client(s)).start();
		new Thread(new Client(s)).start();
	}

	TicketManager tickets;

	private class Client implements Runnable
	{
		public Client(Server s)
		{
			this.s = s;
		}

		public void run()
		{
			while(true)
			{
				/* Grab a ticket */
				int t = tickets.take();

				final int tin = t;
				Runnable run = new Runnable()
				{
					public void run()
					{
						/* Network lag */
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
						s.receive_request(tin);

					}
				};
				new Thread(run).start();

				/* Request some data */
				Object o = tickets.block(t, 5000);
				Integer i = (Integer)o;
				int convert = i;
				System.out.println("number: " + convert);
			}
		}

		private Server s;
	}

	private class Server
	{
		int i;
		public Server()
		{
			i = 1;
		}
		
		void receive_request(int ticket)
		{
			tickets.release(ticket, i++);
		}
	}
}
