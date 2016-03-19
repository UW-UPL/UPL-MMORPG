package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.character.MMOCharacter;

public class PunchAnimation extends Animation
{
	public PunchAnimation(AnimationManager manager, MMOCharacter character,
			double tile_size, AnimationListener listener) 
	{
		super(manager, character, tile_size, listener);
		animating = false;
		lastPunch = 0.0d;
		
		idle = new IdleAnimation(manager, character, tile_size, listener);
	}

	public void setAttacking(MMOCharacter attacking)
	{
		this.attacking = attacking;
	}
	
	@Override
	public void animationInterrupted(Animation source) 
	{
		animating = false;
	}

	@Override
	public void animationStarted() 
	{
		animating = true;
		lastPunch = 0.0d;
		idle.animationStarted();
	}

	@Override
	public void animationReelFinished() 
	{
		idle.animationStarted();
	}

	@Override
	public void animation(double seconds) 
	{
		if(!animating) return;
		
		lastPunch += seconds;
		if(lastPunch >= character.getAttackSpeed())
		{
			/* Punch again */
			lastPunch = 0;
			punch();
		}
	}
	
	private void punch()
	{
		if(!manager.setReel("punch", false))
			throw new RuntimeException("PUNCH ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(50);
	}
	
	@Override
	public void directionChanged(int direction) {}
	
	private boolean animating;
	private double lastPunch;
	private MMOCharacter attacking;
	
	private IdleAnimation idle;
}
