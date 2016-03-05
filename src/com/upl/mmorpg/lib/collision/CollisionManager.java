package com.upl.mmorpg.lib.collision;

import java.util.Iterator;
import java.util.LinkedList;

public class CollisionManager 
{
	public CollisionManager()
	{
		objects = new LinkedList<Collidable>();
		bounds = new LinkedList<Collidable>();
	}
	
	public void addCollidable(Collidable collide)
	{
		objects.add(collide);
	}
	
	public void removeCollidable(Collidable collide)
	{
		objects.remove(collide);
	}
	
	public void addBounds(Collidable collide)
	{
		bounds.add(collide);
	}
	
	public void removeBounds(Collidable collide)
	{
		bounds.remove(collide);
	}
	
	public boolean isColliding(CollideShape shape)
	{
		Iterator<Collidable> it = bounds.iterator();
		while(it.hasNext())
		{
			if(it.next().isColliding(shape))
				return true;
		}
		
		return true;
	}
	
	public boolean isBounded(CollideShape shape)
	{
		Iterator<Collidable> it = bounds.iterator();
		while(it.hasNext())
		{
			if(!it.next().isBounding(shape))
				return false;
		}
		
		return true;
	}
	
	private LinkedList<Collidable> objects;
	private LinkedList<Collidable> bounds;
}
