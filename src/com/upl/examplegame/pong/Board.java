package com.upl.examplegame.pong;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.collision.CollideBox;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class Board extends Renderable
{
	public Board(AssetManager assets, int width, int height)
	{
		super();
		locX = 0;
		locY = 0;
		this.width = width;
		this.height = height;
		this.assets = assets;
		collision = new CollideBox(0, 0, width, height);
		collision_shapes.add(collision);
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		g.drawImage(board_image, (int)locX, (int)locY, (int)width, (int)height, null);
	}
	
	@Override
	public void loadImages() throws IOException
	{
		board_image = assets.loadImage("assets/images/board.png");
	}

	@Override
	public String getRenderName() 
	{
		return "Game Board";
	}
	
	private BufferedImage board_image;
	private AssetManager assets;
	private CollideBox collision;
}
