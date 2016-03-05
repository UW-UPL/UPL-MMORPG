package com.upl.mmorpg.lib.collision;

public abstract class CollideShape 
{
	public abstract boolean inside(double x, double y);
	
	public abstract boolean collidesBox(CollideBox shape);
	public abstract boolean collidesCircle(CollideCircle shape);
	public abstract boolean boundsBox(CollideBox shape);
	public abstract boolean boundsCircle(CollideCircle shape);
}
