package com.upl.mmorpg.lib.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;

public class ExamplePencil extends Renderable
{
	public ExamplePencil()
	{
		points = new LinkedList<Point>();
		hasAnimation = true;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		Iterator<Point> it = points.iterator();
		while(it.hasNext())
		{
			Point p = it.next();
			g.drawRect((int)p.getX(), (int)p.getY(), 1, 1);
		}
	}
	
	@Override
	public void animation(double seconds)
	{
		if((int)locX != (int)lastx || (int)locY != (int)lasty)
		{
			points.add(new Point((int)locX, (int)locY));
			lastx = locX;
			lasty = locY;
		}
	}

	@Override
	public String getRenderName() {
		return "Box";
	}
	
	private LinkedList<Point> points;
	private float lastx;
	private float lasty;
}