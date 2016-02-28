package com.upl.mmorpg.lib.libnet;

import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;

public class TicketManager 
{
	public TicketManager()
	{
		curr_ticket = 1;
		tickets = new LinkedList<Ticket>();
		Log.vtickln("Starting at ticket: " + curr_ticket);
	}
	
	/**
	 * Take a new ticket number.
	 * @return The ticket number
	 */
	public int take()
	{
		int ticket = ticket_take();
		Log.vtickln("Ticket given out: " + ticket);
		return ticket;
	}
	
	/**
	 * Wait on a ticket number for the given number of ms.
	 * @param ticket_num The number of the ticket to wait on
	 * @param timeout The amount of millies MAX to wait for.
	 * @return The result of the ticket on success, null on failure.
	 */
	public Object block(int ticket_num, int timeout)
	{
		Ticket ticket = null;
		try
		{
			/* Create a new ticket object */
			ticket = new Ticket(ticket_num);
			ticket_add(ticket); /* Add the ticket to the queue */
			Thread.sleep(timeout); /* Wait for the ticket to resolve */
			Log.e("DIDN'T GET RESPONSE FOR TICKET " + ticket_num);
			return null; /* Return packet lost */
		} catch(Exception e) 
		{
			Log.vtickln("Got result for ticket " 
					+ ticket_num + " before timeout");
		}
		
		return ticket.result();
	}
	
	/**
	 * Public wrapper for the release method. 
	 * @param ticket_num The ticket to release
	 * @param result The result of the ticket
	 * @return Whether or not the ticket was valid.
	 */
	public boolean release(int ticket_num, Object result)
	{
		return ticket_release(ticket_num, result);
	}
	
	/** 
	 * These 3 are synchronized so no tickets can be added while
	 * searching the queue for removal. Also the current ticket
	 * number needs to be protected from race conditions
	 */
	
	/**
	 * Take a ticket number (securely).
	 * @return The ticket number.
	 */
	private synchronized int ticket_take()
	{
		return curr_ticket++;
	}
	
	/**
	 * Add a ticket to the ticket queue (securely).
	 * @param ticket The ticket to add.
	 */
	private synchronized void ticket_add(Ticket ticket)
	{
		Log.vtickln("Added ticket " + ticket.num() + " to queue");
		tickets.add(ticket);
	}
	
	/**
	 * Let the ticket holder know that the ticket has been
	 * resolved.
	 * @param ticket_num The number of the ticket being resolved
	 * @param result The result of the operation.
	 * @return Whether or not that ticket was valid.
	 */
	public synchronized boolean ticket_release(int ticket_num, Object result)
	{
		Iterator<Ticket> it = tickets.iterator();
		while(it.hasNext())
		{
			Ticket tick = it.next();
			if(tick.num() == ticket_num)
			{
				tick.wake(result);
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized void release_all()
	{
		Iterator<Ticket> it = tickets.iterator();
		while(it.hasNext())
		{
			it.next().wake(null);
			it.remove();
		}
	}
	
	private int curr_ticket; /* Current ticket available (wrapping is ok) */
	private LinkedList<Ticket> tickets; /* The ticket queue */
	
	private class Ticket
	{
		public Ticket(int t)
		{
			this.ticket_num = t;
			this.thread = Thread.currentThread();
		}
		
		public void wake(Object result)
		{
			this.result = result;
			try
			{
				thread.interrupt();
			}catch(Exception e) {}
			Log.vtickln("Ticket " + ticket_num + " got a result.");
		}
		
		public Object result()
		{
			return result;
		}
		
		public int num()
		{
			return ticket_num;
		}
		
		private Thread thread;
		private int ticket_num;
		private Object result;
	}
}
