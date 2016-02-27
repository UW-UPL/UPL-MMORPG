package com.upl.mmorpg.lib.librpc;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.ClientManager;
import com.upl.mmorpg.lib.libnet.ClientManagerListener;
import com.upl.mmorpg.lib.libnet.TicketManager;

public class RPCManager implements ClientManagerListener
{
	public RPCManager(ClientManager client, RPCCallee callee)
	{
		this.client = client;
		this.callee = callee;
		tickets = new TicketManager();
	}
	
	public StackBuffer do_call(StackBuffer buff)
	{
		/* Generate a ticket */
		int ticket = tickets.take();
		/* Append the ticket */
		buff.appendFlag(ticket);
		/* Append the call flag */
		buff.appendFlag(RPC_CALL);
		
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
	
	public void send_result(StackBuffer buff, int ticket)
	{
		/* Append the ticket number for the other end to use */
		buff.appendFlag(ticket);
		/* Append the result flag */
		buff.appendFlag(RPC_RESULT);
		/* Write the result bytes */
		client.writeBytes(buff.toArray());
		/* Flush the stream */
		client.flush();
	}
	
	@Override
	public void bytesReceived(byte[] bytes) 
	{
		StackBuffer buffer = new StackBuffer(bytes);
		int rpc_type = buffer.popInt();
		int ticket_num;
		
		switch(rpc_type)
		{
			case RPC_CALL:
				/* Call the handler */
				callee.handle_call(buffer);
				break;
			case RPC_RESULT:
				ticket_num = buffer.popInt();
				Log.vvln("Received RPC ticket :"+ ticket_num);
				tickets.release(ticket_num, buffer);
				break;
		}
	}

	@Override
	public void connectionLost() 
	{
		Log.vvln("Connection to server lost!!");
	}
	
	private ClientManager client;
	private TicketManager tickets;
	private RPCCallee callee;
	
	private static final int RPC_CALL = 1;
	private static final int RPC_RESULT = 2;
}
