package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class ExampleLine extends Renderable
{
	public ExampleLine(int x1, int y1, int x2, int y2)
	{
		this.locX = x1;
		this.locY = y1;
		this.width = x2;
		this.height = y2;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		g.setColor(Color.BLUE);
		g.drawLine((int)locX, (int)locY, (int)width, (int)height);
	}

	@Override
	public String getRenderName() 
	{
		return "Box";
	}

	@Override
	public String toString() 
	{
		return "ExampleLine";
	}
}
