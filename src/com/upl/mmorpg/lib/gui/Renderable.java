package com.upl.mmorpg.lib.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;

public abstract class Renderable implements Runnable
{
	public Renderable()
	{
		hasAnimation = false;
		animating = false;
		animationThread = null;
		showing = false;
		locX = 0;
		locY = 0;
		width = 0;
		height = 0;
		rotation = 0.0f;
		
		animation_wait = RenderMath.calculateVSYNC(ANIMATION_SPEED);
	}
	
	/**
	 * render the object in the graphics pane
	 * @param g the object to render
	 */
	public abstract void render(Graphics2D g);
	
	/**
	 * Get the name of the render object (Debugging)
	 * @return The name of the object.
	 */
	public abstract String getRenderName();
	
	/**
	 * Load all images that this object will use.
	 */
	public void loadImages() throws IOException {}
	
	public void setX(float n){this.locX = n;}
	public float getX(){return locX;}
	public void setY(float n){this.locY = n;}
	public float getY(){return locY;}
	public void setWidth(float n){this.width = n;}
	public float getWidth(){return width;}
	public void setHight(float n){this.height = n;}
	public float getHeight(){return height;}
	public void setRotation(float degrees){this.rotation = degrees;}
	public float getRotation(){return rotation;}
	
	public void show()
	{
		showing = true;
	}
	
	public void hide()
	{
		showing = false;
	}
	
	public Point getCenter()
	{
		Point p = new Point();
		p.setLocation(locX + (width / 2), locY + (height / 2));
		return p;
	}
	
	public void setCenter(float x, float y)
	{
		locX = x - (width / 2);
		locY = y - (height / 2);
	}
	
	@Override
	public void run()
	{
		while(animating)
		{
			try
			{
				Thread.sleep(animation_wait);
			}catch(Exception e){}
			if(!animating) break;
			
			animation(animation_wait);
		}
	}
	
	protected void animation(double seconds_change) {}
	
	public void startAnimation()
	{
		if(animating || !hasAnimation || true) return;
		
		animating = true;
		animationThread = new Thread(this);
		animationThread.start();
	}
	
	public void stopAnimation()
	{
		animating = false;
		try
		{
			animationThread.interrupt();
		}catch(Exception e){}
		
		try
		{
			animationThread.join(1000);
		}catch(Exception e){}
		
		animationThread = null;
	}
	
	
	protected float locX;
	protected float locY;
	protected float width;
	protected float height;
	protected float rotation;
	
	protected boolean showing;
	protected boolean hasAnimation;
	
	private int animation_wait;
	private Thread animationThread;
	private boolean animating;
	
	private static final int ANIMATION_SPEED = 60;
}
