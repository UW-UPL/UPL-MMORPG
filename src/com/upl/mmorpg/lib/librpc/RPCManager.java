package com.upl.mmorpg.lib.librpc;

import java.io.IOException;
import java.net.Socket;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.NetworkManager;
import com.upl.mmorpg.lib.libnet.NetworkListener;
import com.upl.mmorpg.lib.libnet.TicketManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class RPCManager implements NetworkListener
{
	public RPCManager(Socket socket, int cid, RPCCallee callee) throws IOException
	{
		this.callee = callee;

		tickets = new TicketManager();
		client = new NetworkManager(this, socket, cid);
	}

	public RPCManager(Socket socket, int cid) throws IOException
	{
		this(socket, cid, null);
	}

	public RPCManager(String address, int port, RPCCallee callee) throws IOException
	{
		Socket socket = new Socket(address, port);
		this.callee = callee;

		tickets = new TicketManager();
		client = new NetworkManager(this, socket, 0);
	}

	public void setCallee(RPCCallee callee)
	{
		this.callee = callee;
	}

	public void setListener(RPCListener listen)
	{
		this.listen = listen;
	}

	public StackBuffer do_call(StackBuffer buff, boolean block)
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

		Log.vnetln("Sent out RPC CALL: " + ticket);

		if(!block) return null;

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
		int ticket_num = buffer.popInt();

		switch(rpc_type)
		{
		case RPC_CALL:
			/* Call the handler */
			Log.vvln("Received RPC CALL ticket: "+ ticket_num);
			if(callee != null)
			{
				buffer = callee.handle_call(buffer);
				if(buffer != null)
					send_result(buffer, ticket_num);
			} else Log.e("RPCMANAGER -- NO CALLEE SET!!!\n");
			break;
		case RPC_RESULT:
			Log.vvln("Received RPC RESULT ticket: "+ ticket_num);
			tickets.release(ticket_num, buffer);
			break;
		default:
			Log.e("RECEIVED JUNK");
			break;
		}
	}

	public void shutdown()
	{
		client.shutdown();
		tickets.release_all();
	}

	@Override
	public void connectionLost() 
	{
		/* Just shutdown for now */
		client.shutdown();
		Log.vvln("Connection has been lost!!");
		if(listen != null)
			listen.connectionLost();
	}

	private NetworkManager client;
	private TicketManager tickets;
	private RPCCallee callee;
	private RPCListener listen;

	private static final int RPC_CALL = 1;
	private static final int RPC_RESULT = 2;
}
