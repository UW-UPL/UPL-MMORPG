package com.upl.examplegame.pong;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class Paddle extends Renderable
{
	public Paddle(AssetManager assets, Board board, Player player, int player_num)
	{
		super();
		this.assets = assets;
		this.board = board;
		this.player = player;
		this.player_num = player_num;
		this.paddle_image = -1;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		if(paddle_image >= 0)
		{
			BufferedImage img = assets.getImage(paddle_image);
			g.drawImage(img, (int)locX, (int)locY, (int)width, (int)height, null);
		}
	}
	
	@Override
	public void loadImages() throws IOException
	{
		paddle_image = assets.loadImage("assets/images/paddle.png");
		
		BufferedImage img = assets.getImage(paddle_image);
		this.width = img.getWidth();
		this.height = img.getHeight();
	}

	@Override
	public String getRenderName() 
	{
		return player.getName() + "'s paddle";
	}
	
	private Player player;
	private int player_num;
	private Board board;
	
	private AssetManager assets;
	private int paddle_image;
}
