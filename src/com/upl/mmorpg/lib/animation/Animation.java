package com.upl.mmorpg.lib.animation;

import java.io.Serializable;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;

public abstract class Animation implements Serializable
{
	public Animation(Game game, AnimationManager manager, MMOCharacter character, 
			AnimationListener listener)
	{
		this.game = game;
		this.manager = manager;
		this.character = character;
		
		this.vector_x = 0;
		this.vector_y = 0;
		this.listener = listener;
	}
	
	public abstract void animationInterrupted(Animation source);
	public abstract void animationStarted();
	public abstract void animationReelFinished();
	public abstract void animation(double seconds);
	public abstract void directionChanged(int direction);
	
	protected void generateVectors(int direction)
	{
		switch(direction)
		{
			case AnimationManager.BACK:
				vector_x = 0;
				vector_y = -1;
				break;
			case AnimationManager.BACK_LEFT:
				vector_x = -1;
				vector_y = -1;
				break;
			case AnimationManager.BACK_RIGHT:
				vector_x = 1;
				vector_y = -1;
				break;
			case AnimationManager.RIGHT:
				vector_x = 1;
				vector_y = 0;
				break;
			case AnimationManager.LEFT:
				vector_x = -1;
				vector_y = 0;
				break;
			case AnimationManager.FRONT:
				vector_x = 0;
				vector_y = 1;
				break;
			case AnimationManager.FRONT_RIGHT:
				vector_x = 1;
				vector_y = 1;
				break;
			case AnimationManager.FRONT_LEFT:
				vector_x = -1;
				vector_y = 1;
				break;
		}
	}
	
	public int lookTowards(int row, int col)
	{
		int direction = -1;
		int myRow = character.getRow();
		int myCol = character.getColumn();
		
		if(myRow > row)
		{
			/* Looking up */
			if(myCol > col)
				direction = AnimationManager.BACK_LEFT;
			else if(myCol < col)
				direction = AnimationManager.BACK_RIGHT;
			else direction = AnimationManager.BACK;
		} else if(myRow < row)
		{
			/* Looking down */
			if(myCol > col)
				direction = AnimationManager.FRONT_LEFT;
			else if(myCol < col)
				direction = AnimationManager.FRONT_RIGHT;
			else direction = AnimationManager.FRONT;
		} else {
			/* Either looking left or right */
			if(myCol > col)
				direction = AnimationManager.LEFT;
			else if(myCol < col)
				direction = AnimationManager.RIGHT;
			else direction = AnimationManager.FRONT;
		}
		
		/* Set the direction of the current reel */
		manager.setReelDirection(direction);
		
		return direction;
	}
	
	public void updateTransient(Game game, AnimationListener listener, 
			MMOCharacter character)
	{
		this.game = game;
		this.listener = listener;
		this.character = character;
	}
	
	protected transient Game game;
	protected transient AnimationListener listener;
	protected transient AnimationManager manager;
	protected transient MMOCharacter character;
	
	protected double vector_x;
	protected double vector_y;
	
	private static final long serialVersionUID = 576767154808433026L;
}
