package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;

public class DeathAnimation extends Animation
{
	public DeathAnimation(Game game, AnimationManager manager, MMOCharacter character,
			AnimationListener listener) 
	{
		super(game, manager, character, listener);
		this.game = game;
		disappearTimer = 2.0d;
		disappearSeconds = 0.0d;
		died = false;
	}

	@Override
	public void animationInterrupted(Animation source) {}

	@Override
	public void animationStarted() 
	{
		if(!manager.setReel("death", false))
			throw new RuntimeException("DEATH ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(40);
	}

	@Override
	public void animationReelFinished() 
	{
		died = true;
	}

	@Override
	public void animation(double seconds) 
	{
		if(died)
		{
			disappearSeconds += seconds;
			if(disappearSeconds >= disappearTimer)
			{
				died = false;
				game.removeCharacter(character);
				
				/* Let the quest engine know that this character died */
				game.getQuestEngine().died(character);
			}
		}
	}

	@Override
	public void directionChanged(int direction) {}
	
	private Game game;
	private boolean died;
	private double disappearTimer;
	private double disappearSeconds;
}
