package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.Path;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(AnimationManager animation, 
			MMOCharacter character, double tile_size)
	{
		super(animation, character, tile_size);
		this.animation = animation;
		this.vector_x = 0;
		this.vector_y = 0;
	}
	
	public void walkPath(Path path)
	{
		this.walkingPath = path.copy();
		/* If we're at the first point, remove it */
		path.moveForward();
	}
	
	@Override
	public void animationInterrupted() 
	{
		vector_x = 0;
		vector_y = 0;
	}
	
	@Override
	public void animationStarted() 
	{
		animation.setReel("walk_start", false);
	}
	
	@Override
	public void animationReelFinished() 
	{
		animation.setReel("walk", false);
	}

	@Override
	public void animation(double seconds) 
	{
		double speed = character.getWalkingSpeed();
		double charX = character.getX() + (speed * vector_x);
		double charY = character.getY() + (speed * vector_y);
		
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
		}
	}
	
	@Override
	public void directionChanged(int direction) 
	{
		generateVectors(direction);
	}
	
	private AnimationState state;
	private AnimationManager animation;
	private Path walkingPath;
	
	private enum AnimationState
	{
		NONE, STARTING, WALKING, ENDING
	}
}
