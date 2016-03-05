package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.upl.mmorpg.lib.collision.CollideBox;
import com.upl.mmorpg.lib.collision.CollisionManager;

public class ExampleBox extends Renderable
{
	public ExampleBox(int x, int y, int width, int height)
	{
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		
		collide = new CollideBox(locX, locY, width, height);
	}
	
	public ExampleBox(int x, int y, int width, int height, 
			CollisionManager collision_manager)
	{
		this(x, y, width, height);
		this.collision_manager = collision_manager;
	}
	
	public void enableAnimation()
	{
		this.hasAnimation = true;
		vectX = (Math.random() * 2) - 1;
		vectY = (Math.random() * 2) - 1;
	}
	
	public void disableAnimation()
	{
		this.hasAnimation = false;
		vectX = 0;
		vectY = 0;
	}
	
	@Override
	public void animation(double seconds)
	{
		float locXSave = this.locX;
		float locYSave = this.locY;
		this.locX += vectX * seconds;
		this.locY += vectY * seconds;
		collide.setX(locX);
		collide.setY(locY);
		
		if(!collision_manager.isBounded(collide))
		{
			/* Undo the change */
			this.locX = locXSave;
			this.locY = locYSave;
			
			/* Redo vectors */
			vectX = (Math.random() * 2) - 1;
			vectY = (Math.random() * 2) - 1;
		}
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

	private CollideBox collide;
	private double vectX;
	private double vectY;
}
