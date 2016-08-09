package com.upl.mmorpg.lib.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.collision.Collidable;
import com.upl.mmorpg.lib.collision.CollideBox;
import com.upl.mmorpg.lib.collision.CollideCircle;
import com.upl.mmorpg.lib.collision.CollideShape;
import com.upl.mmorpg.lib.collision.CollisionManager;

public abstract class Renderable implements Runnable, Collidable
{
	public Renderable()
	{
		collision_shapes = new LinkedList<CollideShape>();
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
	
	public void setCollisionManager(CollisionManager collision)
	{
		collision_manager = collision;
	}

	@Override
	public boolean isColliding(CollideShape shape)
	{
		/* Do we have a collision shape? */
		Iterator<CollideShape> it = collision_shapes.iterator();

		while(it.hasNext())
		{
			CollideShape s = it.next();
			if(shape instanceof CollideBox)
			{
				if(s.collidesBox((CollideBox)shape))
				{
					return true;
				}
			} else if(shape instanceof CollideCircle)
			{
				if(s.collidesCircle((CollideCircle)shape))
				{
					return true;
				}
			} else throw new RuntimeException("Unsupported Shape!");
		}

		/* Unsupported shape or no collision */
		return false;
	}
	
	@Override
	public boolean isBounding(CollideShape shape)
	{
		/* Do we have a collision shape? */
		Iterator<CollideShape> it = collision_shapes.iterator();

		while(it.hasNext())
		{
			CollideShape s = it.next();
			if(shape instanceof CollideBox)
			{
				if(!s.boundsBox((CollideBox)shape))
				{
					return false;
				}
			} else if(shape instanceof CollideCircle)
			{
				if(!s.boundsCircle((CollideCircle)shape))
				{
					return false;
				}
			} else throw new RuntimeException("Unsupported Shape!");
		}

		/* Unsupported shape or is bounding */
		return true;
	}
	
	@Override
	public boolean containsShape(CollideShape shape)
	{
		return collision_shapes.contains(shape);
	}

	/**
	 * render the object in the graphics pane
	 * @param g the object to render
	 */
	public abstract void render(Graphics2D g);
	
	/**
	 * Render any additional effects that should be above
	 * characters/objects in the same frame.
	 * @param g The graphics to draw effects.
	 */
	public void renderEffects(Graphics2D g) {}

	/**
	 * Get the name of the render object (Debugging)
	 * @return The name of the object.
	 */
	public abstract String getRenderName();

	/**
	 * Load all images that this object will use.
	 */
	public void loadImages(AssetManager assets) throws IOException {}

	public void setX(double n){this.locX = n;}
	public double getX(){return locX;}
	public void setY(double n){this.locY = n;}
	public double getY(){return locY;}
	public void setWidth(double n){this.width = n;}
	public double getWidth(){return width;}
	public void setHeight(double n){this.height = n;}
	public double getHeight(){return height;}
	public void setRotation(double degrees){this.rotation = degrees;}
	public double getRotation(){return rotation;}

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
	
	public double getCenterX() { return locX + (width / 2); }
	public double getCenterY() { return locY + (height / 2); }
	
	public void setCenter(double x, double y)
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
	
	protected CollisionManager collision_manager;
	protected LinkedList<CollideShape> collision_shapes;
	protected double locX;
	protected double locY;
	protected double width;
	protected double height;
	protected double rotation;

	protected boolean showing;
	protected boolean hasAnimation;

	private int animation_wait;
	private Thread animationThread;
	private boolean animating;

	private static final int ANIMATION_SPEED = 60;
}
