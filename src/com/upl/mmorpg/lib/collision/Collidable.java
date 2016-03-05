package com.upl.mmorpg.lib.collision;

public interface Collidable 
{
	public void setCollisionManager(CollisionManager collision);
	public boolean isColliding(CollideShape shape);
	public boolean isBounding(CollideShape shape);
	public abstract boolean containsShape(CollideShape shape);
}
