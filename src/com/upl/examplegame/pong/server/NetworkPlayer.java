package com.upl.examplegame.pong.server;

import java.io.IOException;
import java.net.Socket;

import com.upl.examplegame.pong.Player;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class NetworkPlayer extends Player
{
	public NetworkPlayer(NetworkGame game, Socket socket, 
			int cid, int player_num) throws IOException 
	{
		super(null, game, player_num);
		
		rpc = new RPCManager(socket, cid, new PongRPCCallee(this));
		caller = new PongRPCCaller(rpc);
		this.ngame = game;
		
		caller.setPlayerNumber(player_num);
	}
	
	public PongRPCCaller getRPCCaller()
	{
		return caller;
	}

	public void updatePaddle(int x, int y)
	{
		ngame.playerMovedPaddle(this, x, y);
	}

	public void puckDeflected(float arg0, float arg1)
	{
		
	}

	public void setName(String name)
	{
		this.name = name;
		ngame.playerSetName(this, name);
		Log.vln("Player setting name: " + name);
	}
	
	public void ready()
	{
		ngame.playerReady(this);
	}
	
	private NetworkGame ngame;
	private RPCManager rpc;
	private PongRPCCaller caller;
}
