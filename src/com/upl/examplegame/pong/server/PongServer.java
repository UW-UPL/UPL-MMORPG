package com.upl.examplegame.pong.server;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.Server;
import com.upl.mmorpg.lib.libnet.ServerListener;

public class PongServer implements ServerListener
{
	public PongServer()
	{
		games = new LinkedList<NetworkGame>();
		server = new Server(this, SERVER_PORT);
		waitingPlayer = null;
	}
	
	public void startServer()
	{
		if(!server.startServer())
		{
			Log.e("Could not start server!");
		}
	}
	
	@Override
	public void acceptClient(Socket socket, int cid) 
	{
		if(waitingPlayer == null)
		{
			waitingPlayer = socket;
			waitingCid = cid;
		} else {
			Log.vln("Staring network game...");
			/* We have enough players for a game */
			try 
			{
				NetworkGame game = new NetworkGame(this,
						waitingPlayer, waitingCid,
						socket, cid);
				games.add(game);
			} catch (IOException e) 
			{
				/* drop both clients */
				try { waitingPlayer.close(); } catch(Exception ex) {}
				try { socket.close(); } catch(Exception ex) {}
				
				waitingPlayer = null;
				socket = null;
			}
			
			waitingPlayer = null;
		}
	}
	
	public void gameEnded(NetworkGame game)
	{
		games.remove(game);
	}
	
	private LinkedList<NetworkGame> games;
	private Socket waitingPlayer;
	private int waitingCid;
	private Server server;
	
	public static void main(String args[])
	{
		new PongServer().startServer();
	}
	
	private static final int SERVER_PORT = 8081;
}
