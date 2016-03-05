package com.upl.mmorpg.lib.collision;

import com.upl.mmorpg.lib.gui.RenderMath;

public class CollideCircle extends CollideShape
{
	@Override
	public boolean inside(double x, double y)
	{
		double dist = RenderMath.pointDistance(centerX, centerY, x, y);
		if(dist > radius) return false;
		return true;
	}
	
	@Override
	public boolean collidesBox(CollideBox shape) 
	{
		return shape.collidesCircle(this);
	}

	@Override
	public boolean collidesCircle(CollideCircle shape) 
	{
		double oradius = shape.getRadius();
		double ox = shape.getCenterX();
		double oy = shape.getCenterY();
		
		double d = RenderMath.pointDistance(centerX, centerY, ox, oy);
				
		if(oradius + radius > d)
			return true;
		return false;
	}
	
	@Override
	public boolean boundsBox(CollideBox shape) 
	{
		double x1 = shape.getX();
		double x2 = x1 + shape.getWidth();
		double y1 = shape.getY();
		double y2 = y1 + shape.getHeight();
		
		if(!inside(x1, y1)) return false;
		if(!inside(x1, y2)) return false;
		if(!inside(x2, y1)) return false;
		return inside(x2, y2);
	}

	@Override
	public boolean boundsCircle(CollideCircle shape) 
	{
		throw new RuntimeException("No Implementation!");
	}
	
	public double getRadius(){return radius;}
	public void setRadius(double r){this.radius = r;}
	public double getCenterX() {return centerX;}
	public double getCenterY() {return centerY;}
	
	public void setCenter(double x, double y)
	{
		this.centerX = x;
		this.centerY = y;
	}
	
	private double centerX;
	private double centerY;
	private double radius;
	
}
