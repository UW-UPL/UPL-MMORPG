package com.upl.mmorpg.lib.libnet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.upl.mmorpg.lib.liblog.Log;

public class ClientManager implements Runnable
{
	private void setup() throws IOException
	{	
		/* Setup the IO streams */
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		
		/* Setup and run the thread */
		running = true;
		thread = new Thread(this);
		thread.start();
		Log.vnetln("Client " + cid + " network setup success.");
	}
	
	public ClientManager(ClientManagerListener cmlisten, 
			Socket socket, int cid) throws IOException
	{
		address = null;
		port = -1;
		this.cmlisten = cmlisten;
		this.socket = socket;
		this.cid = cid;
		setup();
	}
	
	public ClientManager(ClientManagerListener cmlisten, 
			String address, int port, int cid) throws Exception
	{
		this.cmlisten = cmlisten;
		this.address = address;
		this.port = port;
		Log.vnetln("Client " + cid + " connecting to " + address + ":" + port);
		socket = new Socket(address, port);
		this.cid = cid;
		setup();
	}
	
	/**
	 * Send bytes to the client/server.
	 * @param bytes The bytes to send.
	 * @return Whether or not the send was successful.
	 */
	public boolean writeBytes(byte[] bytes)
	{
		if(!running)
			return false;
		/* Write the length of the array */
		try
		{
			dos.writeInt(bytes.length);
			dos.write(bytes, 0, bytes.length);
		} catch (Exception e)
		{
			Log.wtf("Failed to write bytes!", e);
			return false;
		}
		
		Log.vnet("Client " + cid + " wrote " + bytes.length + " bytes.");
		return true;
	}
	
	public boolean flush()
	{
		try
		{
			dos.flush();
			Log.vnet("Client " + cid + " flushed the stream.");
		} catch(Exception e){ return false; }
		return true;
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			Log.vvln("Client " + cid + " is listening.");
			try
			{
				/* Read the length of the message */
				int len = dis.readInt();
				Log.vvln("Client " + cid + " got " + len + " bytes.");
				
				if(len > NetSecurity.MAX_READLEN)
				{
					Log.e("Client " + cid + " read length is too high: "+ len);
					
					/* Ungraceful fail */
					break;
				}
				
				/* Prepare the buffer area */
				byte buffer[] = new byte[len];
				
				/* Read the data */
				dis.read(buffer, 0, len);
				
				if(cmlisten != null)
				{
					cmlisten.bytesReceived(buffer);
				} else {
					Log.vnetln("Client " + cid + " has no listener!");
				}
				buffer = null; /* Free the buffer */
			}catch(Exception e) 
			{
				Log.wtf("Read Fault cid=" + cid, e);
			}
		}
		
		/* We are no longer running */
		running = false;
		
		/* This is where the connection is actually lost */
		if(cmlisten != null)
			cmlisten.connectionLost();
	}
	
	/**
	 * Attempt a reconnect after a lost connection.
	 * @return Whether or not the connection could be established.
	 */
	public boolean tryReconnect()
	{
		if(address == null || port == -1)
			return false;
		
		try
		{
			shutdown();
			socket = new Socket(address, port);
			setup();
		} catch(Exception e)
		{
			Log.vnetln("Client " + cid + " reconnect FAILURE.");
			return false;
		}
		
		Log.vnetln("Client " + cid + " reconnect sucess.");
		return true;
	}
	
	/**
	 * Shutdown the connection. tryReconnect can be used
	 * to reconnect after a shutdown.
	 */
	public void shutdown()
	{
		Log.vvln("Client " + cid + " is shutting down.");
		/* Stop the thread */
		running = false;
		try 
		{
			thread.interrupt();
		} catch(Exception e){}
		try
		{
			thread.wait(1000);
		} catch(Exception e){}
		
		try { dos.close(); } catch (Exception e) {}
		try { dis.close(); } catch (Exception e) {}
		try { socket.close(); } catch (Exception e) {}
		
		dos = null;
		dis = null;
		socket = null;
		thread = null;
	}
	
	/**
	 * Returns whether or not the ClientManager is connected.
	 * @return Connection state.
	 */
	public boolean isConnected()
	{
		return running;
	}
	
	private int cid; /* Client id */
	private Socket socket; /* Socket for network i/o */
	private DataInputStream dis; /* Input stream */
	private DataOutputStream dos; /* Output stream */
	
	private boolean running; /* Are we listening for packets? */
	private Thread thread; /* Seperate thread for listening for packets */
 
	/* Listener we will pass messages to */
	private ClientManagerListener cmlisten;
	
	/* If this is a client connecting to a server, these will be defined: */
	private String address;
	private int port;
}
