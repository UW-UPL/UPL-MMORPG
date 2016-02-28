package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class ExampleBox extends Renderable
{
	public ExampleBox(int x, int y, int width, int height)
	{
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		g.setColor(Color.BLUE);
		g.drawRect((int)locX, (int)locY, (int)width, (int)height);
	}

	@Override
	public String getRenderName() {
		return "Box";
	}

}
