package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.liblog.Log;

/**
 * Animation of the character idling. A timeout value can be set, which
 * after the given amount of milliseconds, the animation will end. A
 * timeout value of -1 can be passed, which means idle forever.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class IdleAnimation extends Animation
{
	public IdleAnimation(Game game, AnimationManager manager, MMOCharacter character,
			AnimationListener listener, int duration) 
	{
		super(game, manager, character, listener, duration);
	}

	@Override
	public void animationInterrupted(Animation animation) {}

	@Override
	public void animationStarted() 
	{
		if(!manager.setReel("idle", true))
			throw new RuntimeException("IDLE ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(5);
	}

	@Override
	public void animationReelFinished() 
	{
		Log.e("Idle animation reel should never finish!!!");
	}

	@Override
	public void animation(double seconds) 
	{
		if(length > 0 && seconds_passed >= length)
			manager.nextAnimation();
	}

	@Override
	public void directionChanged(int direction) {}
	
	private static final long serialVersionUID = 2015342140727303213L;
}
