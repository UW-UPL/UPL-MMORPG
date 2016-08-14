package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class ExampleFilledBox extends ExampleBox
{
	public ExampleFilledBox(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel, double zoom) 
	{
		g.setColor(Color.GREEN);
		g.fillRect((int)(locX * zoom), (int)(locY * zoom), 
				(int)(width * zoom), (int)(height * zoom));
	}

	@Override
	public String getRenderName() 
	{
		return "FilledBox";
	}

	@Override
	public String toString() 
	{
		return "ExampleFilledBox";
	}
}
