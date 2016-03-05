package com.upl.examplegame.pong.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import com.upl.examplegame.pong.Player;
import com.upl.mmorpg.lib.gui.AssetManager;

public class HumanPlayer extends Player implements MouseMotionListener
{
	public HumanPlayer(PongRPCCaller call, AssetManager assets, 
			ClientGame game, int player_num) 
	{
		super(assets, game, player_num);
		this.call = call;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {}
	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		int x = (int)arg0.getX();
		int y = (int)arg0.getY();
		
		paddle.setX(x);
		paddle.setY(y);
		call.updatePaddle(x, y);
	}
	
	private PongRPCCaller call;
}
