package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import com.upl.mmorpg.lib.collision.CollideBox;
import com.upl.mmorpg.lib.collision.CollisionManager;

public class ExampleBox extends Renderable
{
	public ExampleBox(int x, int y, int width, int height)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		
		collide = new CollideBox(locX, locY, width, height);
		collision_shapes.add(collide);
		color = Color.BLUE;
		this.hasAnimation = false;
	}
	
	public ExampleBox(int x, int y, int width, int height, 
			CollisionManager collision_manager)
	{
		this(x, y, width, height);
		this.collision_manager = collision_manager;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public void enableAnimation()
	{
		Random random = new Random(System.nanoTime());
		this.hasAnimation = true;
		int pixles_per_second = 250;
		int min_pps = 0;
		vectX = (random.nextInt(2) == 1 ? 1 : -1) 
				* (min_pps + (Math.random() * pixles_per_second));
		vectY = (random.nextInt(2) == 1 ? 1 : -1) 
				* (min_pps + (Math.random() * pixles_per_second));
		
		/* Generate a new color */
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		
		color = new Color(r, g, b);
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
		double locXSave = this.locX;
		double locYSave = this.locY;
		this.locX += vectX * seconds;
		this.locY += vectY * seconds;
		collide.setX(locX);
		collide.setY(locY);
		
		if(!collision_manager.isBounded(collide)
				)//|| collision_manager.isColliding(collide))
		{
			/* Undo the change */
			this.locX = locXSave;
			this.locY = locYSave;
			
			/* Redo vectors */
			enableAnimation();
		}
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel, double zoom) 
	{
		g.setColor(color);
		g.fillRect((int)(locX * zoom), (int)(locY * zoom), 
				(int)(width * zoom), (int)(height * zoom));
	}

	@Override
	public String getRenderName() {
		return "Box";
	}
	
	@Override
	public void setX(double x)
	{
		super.setX(x);
		collide.setX(x);
	}
	
	@Override
	public void setY(double y)
	{
		super.setY(y);
		collide.setY(y);
	}
	
	@Override
	public void setWidth(double width)
	{
		super.setWidth(width);
		collide.setWidth(width);
	}
	
	@Override
	public void setHeight(double height)
	{
		super.setHeight(height);
		collide.setHeight(height);
	}
	
	@Override
	public String toString() 
	{
		return "Example Box (" + locX + ", " + locY + ", "
				+ (width + locX) + ", " + (locY + height) + ")";
	}

	private Color color;
	private CollideBox collide;
	private double vectX;
	private double vectY;
}
