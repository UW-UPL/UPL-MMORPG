package com.upl.mmorpg.lib.animation;

import java.io.IOException;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.liblog.Log;

/**
 * Base class for all complex animations in UPL-MMORPG. Complex animations
 * have their own animation managers with their own animation queue. More
 * complicated animations can have complex animations in their animation
 * queues.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public abstract class ComplexAnimation extends Animation
{
	public ComplexAnimation(Game game, AnimationManager manager, MMOCharacter character)
	{
		super(game, null, character, 1);
		this.subManager = new ComplexAnimationManager(manager, game, character, this);
		this.manager = subManager;
		this.mainManager = manager;
	}
	
	public AnimationManager getManager()
	{
		return manager;
	}
	
	@Override
	public void animationStopped() 
	{
		manager.stopManaging();
		mainManager.clearSubManager();
	}
	
	@Override
	public void interrupt() 
	{ 
		manager.stopManaging();
		manager.clearSubManager();
		mainManager.nextAnimation();
	}
	
	@Override
	public void animationStarted() 
	{
		mainManager.setSubManager(manager);
		manager.animationChanged();
	}
	
	/**
	 * Called when the animation is about to change.
	 */
	public abstract void nextAnimation();
	
	@Override public void animationReelFinished() { }
	@Override public void animation(double seconds) {}
	
	@Override
	public void updateTransient(Game game, MMOCharacter character, AnimationManager manager)
	{
		/* Update the parent for the sub manager */
		subManager.parent = this;
		
		/* Update the other properties for this animation */
		super.updateTransient(game, character, subManager);
		this.mainManager = manager;
		this.manager = subManager;
		
		try 
		{
			subManager.updateTransient(game, character);
		} catch (IOException e) 
		{
			Log.wtf("Couldn't update transient for complex animation!", e);
		}
	}
	
	@Override
	public String toString()
	{
		return "Complex Animation";
	}
	
	protected ComplexAnimationManager subManager;
	protected transient AnimationManager mainManager;
	
	public static class ComplexAnimationManager extends AnimationManager
	{
		/**
		 * Make a copy of an animation manager.
		 * @param manager The animation manager to copy.
		 * @param game The current game.
		 * @param character The character we are animating.
		 */
		public ComplexAnimationManager(AnimationManager manager, Game game, MMOCharacter character, ComplexAnimation parent)
		{
			super(game, character);
			this.parent = parent;
			this.assets = manager.assets;
			this.currentReel = null;
			this.reelPos = -1;
			this.reelDirection = FRONT;
			this.currentFrame = null;
			this.animation_total = manager.animation_total;
			this.animation_speed = manager.animation_speed;
			this.animating = false;
			this.currentReelName = manager.currentReelName;
			this.reelsPath = manager.reelsPath;
			this.subManager = null;
			this.map = manager.map;
			this.animationQueue = new AnimationQueue(this, new IdleAnimation(game, this, character, -1));
		}
		
		@Override
		public synchronized void nextAnimation()
		{
			super.nextAnimation();
			Log.vln("ComplexAnimation -- nextAnimation");
			parent.nextAnimation();
		}
		
		private transient ComplexAnimation parent;
		
		private static final long serialVersionUID = -1407868312520894727L;
	}
	
	private static final long serialVersionUID = 484663537164655377L;
}
