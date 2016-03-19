package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;

public class IdleAnimation extends Animation
{
	public IdleAnimation(Game game, AnimationManager manager, MMOCharacter character,
			double tile_size, AnimationListener listener) {
		super(game, manager, character, tile_size, listener);
	}

	@Override
	public void animationInterrupted(Animation source) {}

	@Override
	public void animationStarted() 
	{
		if(!manager.setReel("idle", true))
			throw new RuntimeException("IDLE ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(5);
	}

	@Override
	public void animationReelFinished() {}

	@Override
	public void animation(double seconds) {}

	@Override
	public void directionChanged(int direction) {}
}
