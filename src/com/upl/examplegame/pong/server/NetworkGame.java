package com.upl.examplegame.pong.server;

import java.io.IOException;
import java.net.Socket;

import com.upl.examplegame.pong.Game;
import com.upl.examplegame.pong.Player;

public class NetworkGame extends Game
{
	public NetworkGame(PongServer server, 
			Socket player1Socket, int cid1,
			Socket player2Socket, int cid2) throws IOException
	{
		super();
		
		player1 = nplayer1 = new NetworkPlayer(this, player1Socket, cid1, 1);
		player2 = nplayer2 = new NetworkPlayer(this, player2Socket, cid2, 2);
		
		player1Ready = false;
		player2Ready = false;
	}
	
	public void playerMovedPaddle(Player player, float x, float y)
	{
		if(player1 == player)
			nplayer2.getRPCCaller().otherPlayerPaddle(x, y);
		else nplayer1.getRPCCaller().otherPlayerPaddle(x, y);
	}
	
	public synchronized void playerSetName(Player player, String name)
	{
		if(player1.getName() == null || player2.getName() == null)
			return;
		
		nplayer1.getRPCCaller().setOpponentName(player2.getName());
		nplayer2.getRPCCaller().setOpponentName(player1.getName());
	}
	
	public synchronized void playerReady(Player player)
	{
		if(player == player1)
			player1Ready = true;
		else if(player == player2)
			player2Ready = true;
		
		if(player1Ready && player2Ready)
		{
			/* Start a fresh round */
			puck.generate_properties();
			puck.setCenter(board.getWidth() / 2, board.getHeight() / 2);
			nplayer1.getRPCCaller().setPuckProperties(
					puck.getX(), puck.getY(), 
					puck.getDirection(), puck.getVelocity(), 
					puck.getSpin());
			nplayer2.getRPCCaller().setPuckProperties(
					puck.getX(), puck.getY(), 
					puck.getDirection(), puck.getVelocity(),
					puck.getSpin());
		}
	}
	
	private PongServer server;
	
	private NetworkPlayer nplayer1;
	private NetworkPlayer nplayer2;
	
	private boolean player1Ready;
	private boolean player2Ready;
}
