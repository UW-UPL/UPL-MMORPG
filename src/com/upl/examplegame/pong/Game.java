package com.upl.examplegame.pong;

import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;

public class Game 
{
	public Game() throws IOException
	{
		/* Setup the game components */
		assets = new AssetManager();
		board = new Board(assets, BOARD_WIDTH, BOARD_HEIGHT);
		puck = new Puck(assets);
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	protected AssetManager assets;
	protected Board board;
	protected Player player1;
	protected Player player2;
	
	protected Puck puck;
	
	private static final int BOARD_WIDTH = 800;
	private static final int BOARD_HEIGHT = 600;
}
