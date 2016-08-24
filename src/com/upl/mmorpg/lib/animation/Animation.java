package com.upl.mmorpg.lib.animation;

import java.io.Serializable;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;

/**
 * Base, abstract class for an animation. This animation class can only currently
 * apply to MMOCharacters and not other objects.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public abstract class Animation implements Serializable
{
	/**
	 * Initialize the animation object.
	 * @param game The current game in which the character is playing.
	 * @param manager The animation manager we should use for reels and other assets.
	 * @param character The character we are animating.
	 * @param listener The listener we should send animation events to.
	 * @param length The estimated length of the animation (milliseconds). -1 for infinite.
	 */
	public Animation(Game game, AnimationManager manager, MMOCharacter character, int length)
	{
		this.game = game;
		this.manager = manager;
		this.character = character;
		
		this.vector_x = 0;
		this.vector_y = 0;
		this.length = ((double)length) / 1000.0d;
		seconds_passed = 0.0d;
	}
	
	/**
	 * Called when the current animation should stop as soon as possible
	 * and tell the manager to move to the next animation.
	 */
	public abstract void interrupt();
	
	/**
	 * The current animation is now in the foreground.
	 */
	public abstract void animationStarted();
	
	/**
	 * The reel that was currently playing for this animation has finished.
	 */
	public abstract void animationReelFinished();
	
	/**
	 * Do any calculations needed for the animation.
	 * @param seconds The amount of seconds that have passed since the
	 * last animation (usually a very very small fraction).
	 */
	public abstract void animation(double seconds);
	
	/**
	 * Notification telling the animation that it is being removed from the animation
	 * queue either because it told the animation manager it is done or the animation
	 * manager needs to remove it from the queue.
	 */
	public abstract void animationStopped();
	
	/**
	 * Called by the animation manager. This just prepares the actual animation
	 * for rendering. This should NOT be overridden, animation should be overridden
	 * instead.
	 * @param seconds The amount of seconds that have passed.
	 */
	public final void doAnimation(double seconds)
	{
		this.seconds_passed += seconds;
		this.animation(seconds);
	}
	
	/**
	 * Generate new x/y vectors for the given direction. These can be
	 * multiplied against a speed vector and the amount of seconds passed
	 * to calculate the amount of distance changed over a small amount of
	 * time.
	 * @param direction The new direction for the animation.
	 */
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
	
	/**
	 * Force the character to look in the direction of a square.
	 * @param row The row of the square to look at.
	 * @param col The column of the square to look at.
	 * @return The direction the character is now looking.
	 */
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
			/* Either looking left or right or on the same square */
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
	
	/**
	 * Returns the amount of seconds this animation has spent animating.
	 * @return The amount of seconds this animation has spent animating.
	 */
	public double getAnimationSeconds()
	{
		return seconds_passed;
	}
	
	/**
	 * Returns the estimated amount of milliseconds it will
	 * take for this animation to complete.
	 * @return The estimated amount of milliseconds this animation will take.
	 */
	public double getLength()
	{
		return length;
	}
	
	/**
	 * Return the name of this animation (debug).
	 */
	public String toString()
	{
		return "Base animation";
	}
	
	/**
	 * Update any properties that wouldn't have transferred over the network properly.
	 * @param game The current game.
	 * @param listener The animation listener for this animation.
	 * @param character The character we are animating.
	 */
	public void updateTransient(Game game, MMOCharacter character, AnimationManager manager)
	{
		this.game = game;
		this.character = character;
		this.manager = manager;
	}
	
	protected transient Game game; /**< The current game. */
	protected transient AnimationManager manager; /**< The animation manager that manages reels and other assets. */
	protected transient MMOCharacter character; /**< The character that we are animating. */
	
	protected double vector_x; /**< The positive or negative X direction we are moving (either 1 or -1). */
	protected double vector_y; /**< The positive or negative Y direction we are moving (either 1 or -1). */
	protected double length; /**< The estimated amount of time this animation will take (seconds). */
	protected double seconds_passed; /**< The amount of seconds this animation has been going.  */
	
	private static final long serialVersionUID = 576767154808433026L;
}
