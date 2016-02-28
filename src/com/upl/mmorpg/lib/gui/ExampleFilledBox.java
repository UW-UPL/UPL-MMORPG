package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class ExampleFilledBox extends Renderable
{
	public ExampleFilledBox(int x, int y, int width, int height)
	{
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		g.setColor(Color.GREEN);
		g.fillRect((int)locX, (int)locY, (int)width, (int)height);
	}

	@Override
	public String getRenderName() {
		return "FilledBox";
	}

}
