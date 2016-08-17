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
	public void render(Graphics2D g, RenderPanel panel) 
	{
		g.setColor(Color.GREEN);
		fillRect(panel, g, locX, locY, width, height);
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
	
	private static final long serialVersionUID = -3948672684967632088L;
}
