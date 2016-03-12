package com.upl.mmorpg.lib.animation;

import java.util.Iterator;

import com.upl.mmorpg.game.character.FollowListener;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.Path;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(AnimationManager animation, 
			MMOCharacter character, double tile_size, 
			AnimationListener listener)
	{
		super(animation, character, tile_size, listener);
		this.vector_x = 0;
		this.vector_y = 0;
		arrived = false;
		interrupted =  false;
	}
	
	public void setPath(Path path)
	{
		if(path == null) return;
		this.walkingPath = path;
		interrupted = false;
		
		/* If we're at the first point, remove it */
		int myRow = (int)(character.getCenterY() / tile_size);
		int myCol = (int)(character.getCenterX() / tile_size);
		while(this.walkingPath.getNextCol() == myCol 
				&& this.walkingPath.getNextRow() == myRow)
			this.walkingPath.moveForward();
	}
	
	@Override
	public void animationInterrupted(Animation source) 
	{
		vector_x = 0;
		vector_y = 0;
	}
	
	@Override
	public void animationStarted() 
	{
		if(walkingPath == null) return;
		arrived = false;
		interrupted = false;
		if(!manager.setReel("walk_start", false))
			throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(100);
		
		int dir = this.lookTowards(walkingPath.getNextRow(), walkingPath.getNextCol());
		manager.setReelDirection(dir);
		this.generateVectors(dir);
		
		Iterator<FollowListener> it = character.getFollowers();
		while(it.hasNext())
		{
			it.next().characterMoving(character, 
					character.getRow(), 
					character.getCol());
		}
	}
	
	@Override
	public void animationReelFinished() 
	{
		interrupted = false;
		if(!arrived)
		{
			if(!manager.setReel("walk", false))
				throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
			manager.setAnimationSpeed(15);
		} else {
			if(listener != null)
				listener.animationFinished();
		}
	}
	
	public synchronized void interruptPath()
	{
		interrupted = true;
	}

	@Override
	public void animation(double seconds) 
	{
		if(walkingPath == null) return;
		
		double speed = character.getWalkingSpeed();
		double charX = character.getX() + (speed * vector_x * seconds * tile_size);
		double charY = character.getY() + (speed * vector_y * seconds * tile_size);
		
		double destX = walkingPath.getNextCol() * tile_size;
		double destY = walkingPath.getNextRow() * tile_size;
		
		double diffX = destX - charX;
		double diffY = destY - charY;
		
		if(diffX > 0) diffX = 1;
		else if(diffX == 0) diffX = 0;
		else diffX = -1;
		if(diffY > 0) diffY = 1;
		else if(diffY == 0) diffY = 0;
		else diffY = -1;
		
		if(diffX != vector_x || diffY != vector_y)
		{
			
			/* We passed our destination */
			character.setX(destX);
			character.setY(destY);
			walkingPath.moveForward();
			
			if(walkingPath.isEmpty() || interrupted)
			{
				walkingPath = null;
				arrived = true;
				
				if(!interrupted)
				{
					manager.setAnimationSpeed(30);
					manager.setReel("walk_end", false);
				} else this.animationReelFinished();
			} else {
				/* Change direction properties */
				int dir = this.lookTowards(walkingPath.getNextRow(), 
						walkingPath.getNextCol());
				manager.setReelDirection(dir);
				this.generateVectors(dir);
				
				Iterator<FollowListener> it = character.getFollowers();
				while(it.hasNext())
				{
					it.next().characterMoving(character, 
							character.getRow(), 
							character.getCol());
				}
			}
		} else {
			character.setX(charX);
			character.setY(charY);
		}
	}
	
	@Override
	public void directionChanged(int direction) 
	{
		generateVectors(direction);
	}
	
	private Path walkingPath;
	private boolean arrived;
	private boolean interrupted;
}
