package com.upl.examplegame.pong;

import com.upl.mmorpg.lib.gui.AssetManager;

public class Player 
{
	public Player(AssetManager assets, Game game, int player_num)
	{
		this.game = game;
		paddle = new Paddle(assets, game.getBoard(), this, player_num);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public Paddle getPaddle()
	{
		return paddle;
	}
	
	protected Game game;
	protected Paddle paddle;
	protected String name;
	protected int score;
}
