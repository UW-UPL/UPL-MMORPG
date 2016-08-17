package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import com.upl.mmorpg.lib.collision.CollideCircle;
import com.upl.mmorpg.lib.collision.CollisionManager;

public class ExampleCircle extends Renderable
{
	public ExampleCircle(double x, double y, double radius)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.radius = radius;
		
		collide = new CollideCircle(x, y, radius);
		collision_shapes.add(collide);
		color = Color.BLUE;
		this.hasAnimation = false;
	}
	
	public ExampleCircle(double x, double y, double radius, 
			CollisionManager collision_manager)
	{
		this(x, y, radius);
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
		collide.setCenter(locX, locY);
		
		if(!collision_manager.isBounded(collide)
				|| collision_manager.isColliding(collide))
		{
			/* Undo the change */
			this.locX = locXSave;
			this.locY = locYSave;
			
			/* Redo vectors */
			enableAnimation();
		}
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel) 
	{
		g.setColor(color);
		double x = this.locX - (radius);
		double y = this.locY - (radius);
		fillOval(panel, g, x, y, radius * 2, radius * 2);
	}

	@Override
	public String getRenderName() {
		return "Circle";
	}
	
	@Override
	public void setX(double x)
	{
		super.setX(x);
		collide.setCenter(x, locY);
	}
	
	@Override
	public void setY(double y)
	{
		super.setY(y);
		collide.setCenter(locX, y);
	}
	
	@Override
	public void setWidth(double width)
	{
		super.setWidth(width);
	}
	
	@Override
	public void setHeight(double height)
	{
		super.setHeight(height);
	}
	
	@Override
	public void setCenter(double x, double y)
	{
		locX = x;
		locY = y;
		collide.setCenter(x, y);
	}
	
	@Override
	public String toString() 
	{
		return "Example Circle (" + locX + ", " + locY + ", "
				+ (width + locX) + ", " + (locY + height) + ")";
	}

	private transient Color color;
	private transient CollideCircle collide;
	private transient double vectX;
	private transient double vectY;
	private transient double radius;
	
	private static final long serialVersionUID = -518345372873078336L;
}
