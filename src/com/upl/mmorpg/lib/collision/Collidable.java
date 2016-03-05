package com.upl.mmorpg.lib.collision;

public interface Collidable 
{
	public boolean isColliding(CollideShape shape);
	public boolean isBounding(CollideShape shape);
}
