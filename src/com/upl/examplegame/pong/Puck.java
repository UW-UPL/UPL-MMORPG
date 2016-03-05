package com.upl.examplegame.pong;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.collision.CollideCircle;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class Puck extends Renderable
{
	public Puck(AssetManager assets)
	{
		super();
		this.assets = assets;
		
		width = PUCK_WIDTH;
		height = PUCK_HEIGHT;
		
		hasAnimation = true;
		
		collision = new CollideCircle(0, 0, PUCK_WIDTH / 2);
		collision_shapes.add(collision);
	}
	
	public void loadImages() throws IOException
	{
		puck_image = assets.loadImage("assets/images/pong_puck.png");
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		if(!showing) return;

		/* Save the state */
		AffineTransform state = g.getTransform();
		
		Point center = this.getCenter();
		
		AffineTransform new_state = new AffineTransform();
        new_state.translate(center.getX(), center.getY());
        new_state.rotate(rotation);
		g.setTransform(new_state);
		BufferedImage img = assets.getImage(puck_image);
		g.drawImage(img, -(int)(width / 2), -(int)(height / 2), 
				(int)width, (int)height, null);
		//g.fillOval(-(int)width / 2, -(int)height / 2, (int)width, (int)height);
		
		/* Restore old state */
		g.setTransform(state);
	}
	
	@Override
	public void animation(double seconds_change)
	{
		/* Rotate the puck slowly */
		double part_velocity = velocity * seconds_change;
		double changeX = Math.cos(direction) * part_velocity;
		double changeY = Math.sin(direction) * part_velocity;
		
		double locXSave = this.locX;
		double locYSave = this.locY;
		
		this.locX += changeX;
		this.locY += changeY;
		this.rotation += seconds_change * this.spin * Math.PI * 2;
		collision.setCenter(locX, locY);
		
		if(!collision_manager.isBounded(collision)
				|| collision_manager.isColliding(collision))
		{
			/* Undo the change */
			this.locX = locXSave;
			this.locY = locYSave;
			
			/* Redo vectors */
			//enableAnimation();
		}
	}
	
	@Override
	public void setX(double x)
	{
		super.setX(x);
		collision.setCenter(x, locY);
	}
	
	@Override
	public void setY(double y)
	{
		super.setY(y);
		collision.setCenter(locX, y);
	}
	
	@Override
	public Point getCenter()
	{
		Point p = new Point();
		p.setLocation(locX, locY);
		return p;
	}
	
	@Override
	public void setCenter(double x, double y)
	{
		locX = x;
		locY = y;
		collision.setCenter(x, y);
	}
	
	public void generate_properties()
	{
		/* Should be able to go */
		this.direction = (float)(Math.random() * Math.PI * 2);
		this.velocity = 100 + (float)(Math.random() * 100);
		this.spin = (float)(Math.random());
	}

	@Override
	public String getRenderName() 
	{
		return "Pong Puck";
	}
	
	public void setVelocity(double velocity){this.velocity = velocity;}
	public void setDirection(double direction){this.direction = direction;}
	public void setSpin(double spin){this.spin = spin;}
	public double getVelocity(){return velocity;}
	public double getDirection(){return direction;}
	public double getSpin(){return spin;}
	
	private AssetManager assets;
	private int puck_image;
	
	private double direction; /* direction in Radians */
	private double velocity; /* Velocity in pixles/second */
	private double spin; /* Set the spin (roatations per second )*/
	
	private CollideCircle collision;
	
	private static final int PUCK_WIDTH = 100;
	private static final int PUCK_HEIGHT = 100;
}
