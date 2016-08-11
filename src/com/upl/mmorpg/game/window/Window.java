package com.upl.mmorpg.game.window;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;

public abstract class Window extends Renderable{
	
	public Window(double x, double y, AssetManager assets, Game game)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.assets = assets;
		this.game = game;
		this.width = 200;
		this.height= 400;
	}
	
	@Override
	public void render(Graphics2D g)
	{
		BufferedImage img = null;
		try {
			img = assets.loadImage("assets/images/windows/Untitled.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(img == null) return;
		
		g.drawImage(img, 
				(int)locX, (int)locY, (int)width, (int)height, null);
	}

	@Override
	public String getRenderName() 
	{
		return "Window";
	}
	
	public boolean isHovered(){
		return isHovered;
	}
	
	protected AssetManager assets;
	protected Game game; 
	protected boolean isHovered;

}
