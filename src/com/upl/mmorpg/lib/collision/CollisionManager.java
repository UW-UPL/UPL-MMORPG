package com.upl.mmorpg.lib.collision;

import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;

public class CollisionManager 
{
	public CollisionManager()
	{
		objects = new LinkedList<Collidable>();
		bounds = new LinkedList<Collidable>();
	}
	
	public synchronized void addCollidable(Collidable collide)
	{
		objects.add(collide);
	}
	
	public synchronized void removeCollidable(Collidable collide)
	{
		objects.remove(collide);
	}
	
	public synchronized void addBounds(Collidable collide)
	{
		bounds.add(collide);
	}
	
	public synchronized void removeBounds(Collidable collide)
	{
		bounds.remove(collide);
	}
	
	public boolean isColliding(CollideShape shape)
	{
		Iterator<Collidable> it = objects.iterator();
		while(it.hasNext())
		{
			Collidable c = it.next();
			if(c.containsShape(shape)) continue;
			if(c.isColliding(shape))
			{
				Log.collln(shape  + " has collided with " + c);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isBounded(CollideShape shape)
	{
		Iterator<Collidable> it = bounds.iterator();
		while(it.hasNext())
		{
			Collidable c = it.next();
			if(c.containsShape(shape)) continue;
			if(!c.isBounding(shape))
			{
				Log.collln(c + " is not bounding " + shape);
				return false;
			}
		}
		
		return true;
	}
	
	private LinkedList<Collidable> objects;
	private LinkedList<Collidable> bounds;
}
