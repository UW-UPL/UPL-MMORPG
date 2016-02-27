package com.upl.mmorpg.lib.librpc;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.ClientManager;
import com.upl.mmorpg.lib.libnet.ClientManagerListener;
import com.upl.mmorpg.lib.libnet.TicketManager;

public class RPCManager implements ClientManagerListener
{
	public RPCManager(ClientManager client)
	{
		this.client = client;
		tickets = new TicketManager();
	}
	
	public StackBuffer do_call(StackBuffer buff)
	{
		/* Generate a ticket */
		int ticket = tickets.take();
		/* Append the ticket */
		buff.appendFlag(ticket);
		
		/* Send the bytes */
		client.writeBytes(buff.toArray());
		client.flush();
		
		/* Wait on the ticket */
		StackBuffer result = (StackBuffer)
				tickets.block(ticket, 5000);
		
		if(result == null)
			throw new RPCTimeoutException();
		
		return result;
	}
	
	@Override
	public void bytesReceived(byte[] bytes) 
	{
		StackBuffer buffer = new StackBuffer(bytes);
		int ticket_num = buffer.popInt();
		Log.vvln("Received RPC ticket :"+ ticket_num);
		tickets.release(ticket_num, buffer);
	}

	@Override
	public void connectionLost() 
	{
		Log.vvln("Connection to server lost!!");
	}
	
	private ClientManager client;
	private TicketManager tickets;
}
