package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.Path;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(AnimationManager animation, 
			MMOCharacter character, double tile_size)
	{
		super(animation, character, tile_size);
		this.vector_x = 0;
		this.vector_y = 0;
		arrived = false;
	}
	
	public void setPath(Path path)
	{
		this.walkingPath = path;
		
		/* If we're at the first point, remove it */
		int myRow = (int)(character.getCenterY() / tile_size);
		int myCol = (int)(character.getCenterX() / tile_size);
		while(this.walkingPath.getNextCol() == myCol 
				&& this.walkingPath.getNextRow() == myRow)
			this.walkingPath.moveForward();
		
		System.out.println("MR: " + myRow + " MC: " + myCol +"  nr: " 
		+ walkingPath.getNextRow() + " nc: " + walkingPath.getNextCol());
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
		arrived = false;
		if(!manager.setReel("walk_start", false))
			throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(20);
		
		System.out.println("  nr: " + walkingPath.getNextRow() + " nc: " + walkingPath.getNextCol());
		int dir = this.lookTowards(walkingPath.getNextRow(), walkingPath.getNextCol());
		manager.setReelDirection(dir);
		this.generateVectors(dir);
	}
	
	@Override
	public void animationReelFinished() 
	{
		if(!arrived)
		{
			if(!manager.setReel("walk", false))
				throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
			manager.setAnimationSpeed(15);
		}
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
		
		System.out.println("DIFFX: " + diffX + " DIFFY: " + diffY + "   vectX: " + vector_x + " vectY: " + vector_y);
		System.out.println("charX: " + charX + "  charY: " + charY + " destX; " + destX + " destY: " + destY);
		
		if(diffX != vector_x || diffY != vector_y)
		{
			System.out.println("Got to " + walkingPath.getNextRow() + "  " + walkingPath.getNextCol());
			/* We passed our destination */
			character.setX(destX);
			character.setY(destY);
			walkingPath.moveForward();
			
			if(walkingPath.isEmpty())
			{
				walkingPath = null;
				arrived = true;
				manager.setAnimationSpeed(30);
				manager.setReel("walk_end", false);
			} else {
				/* Change direction properties */
				int dir = this.lookTowards(walkingPath.getNextRow(), walkingPath.getNextCol());
				manager.setReelDirection(dir);
				this.generateVectors(dir);
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
	
	private AnimationState state;
	private Path walkingPath;
	private boolean arrived;
	
	private enum AnimationState
	{
		NONE, STARTING, WALKING, ENDING
	}
}
