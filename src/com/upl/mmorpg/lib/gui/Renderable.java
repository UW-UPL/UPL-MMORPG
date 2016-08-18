package com.upl.mmorpg.lib.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.collision.Collidable;
import com.upl.mmorpg.lib.collision.CollideBox;
import com.upl.mmorpg.lib.collision.CollideCircle;
import com.upl.mmorpg.lib.collision.CollideShape;
import com.upl.mmorpg.lib.collision.CollisionManager;

public abstract class Renderable implements Serializable, Collidable
{
	public Renderable()
	{
		collision_shapes = new LinkedList<CollideShape>();
		hasAnimation = false;
		showing = false;
		renderable = true;
		locX = 0;
		locY = 0;
		width = 0;
		height = 0;
		rotation = 0.0f;
	}
	
	public boolean isRenderable()
	{
		return renderable;
	}
	
	public boolean inGlass()
	{
		return inGlass;
	}
	
	public void setInGlass(boolean inGlass)
	{
		this.inGlass = inGlass;
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
	public abstract void render(Graphics2D g, RenderPanel parent);
	
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
	
	public void setLocation(double x, double y)
	{
		this.locX = x;
		this.locY = y;
	}
	
	public void setSize(double size)
	{
		this.width = size;
		this.height = size;
	}

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

	protected void animation(double seconds_change) {}

	public void drawImage(RenderPanel parent, Graphics2D g, BufferedImage img, double x, double y, 
			double width, double height)
	{
		double zoom = parent.getZoom();
		g.drawImage(img, (int)(x * zoom), (int)(y * zoom), 
				(int)(width * zoom), (int)(height * zoom), null);
	}
	
	public void drawString(RenderPanel parent, Graphics2D g, String text, double x, double y)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.drawString(text, (int)(x), (int)(y));
		else
			g.drawString(text, (int)(x * zoom), (int)(y * zoom));
	}
	
	public void drawLine(RenderPanel parent, Graphics2D g, double x1, double y1, 
			double x2, double y2)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.drawLine((int)(x1), (int)(y1), (int)(x2), (int)(y2));
		else 
			g.drawLine((int)(x1 * zoom), (int)(y1 * zoom), (int)(x2 * zoom), (int)(y2 * zoom));
	}
	
	public void drawOval(RenderPanel parent, Graphics2D g, double x, double y,
			double width, double height)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.drawOval((int)(x), (int)(y), (int)(width), (int)(height));
		else
			g.drawOval((int)(x * zoom), (int)(y * zoom), (int)(width * zoom), (int)(height * zoom));
	}
	
	public void fillOval(RenderPanel parent, Graphics2D g, double x, double y,
			double width, double height)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.fillOval((int)(x), (int)(y), (int)(width), (int)(height));
		else
		g.fillOval((int)(x * zoom), (int)(y * zoom), (int)(width * zoom), (int)(height * zoom));
	}
	
	public void drawRect(RenderPanel parent, Graphics2D g, double x, double y,
			double width, double height)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.drawRect((int)(x), (int)(y), (int)(width), (int)(height));
		else
		g.drawRect((int)(x * zoom), (int)(y * zoom), (int)(width * zoom), (int)(height * zoom));
	}
	
	public void fillRect(RenderPanel parent, Graphics2D g, double x, double y,
			double width, double height)
	{
		double zoom = parent.getZoom();
		if(inGlass)
			g.fillRect((int)(x), (int)(y), (int)(width), (int)(height));
		else
		g.fillRect((int)(x * zoom), (int)(y * zoom), (int)(width * zoom), (int)(height * zoom));
	}
	
	protected transient CollisionManager collision_manager;
	protected transient LinkedList<CollideShape> collision_shapes;
	protected double locX;
	protected double locY;
	protected double width;
	protected double height;
	protected double rotation;

	protected boolean showing;
	protected boolean hasAnimation;
	protected boolean renderable; /**< Whether or not this renderable is ready to render */
	protected boolean inGlass; /**< Whether or not this renderable is in the glass pane */

	private static final long serialVersionUID = 3852771086756115110L;
}
