package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;

public class DeathAnimation extends Animation
{
	public DeathAnimation(Game game, AnimationManager manager, MMOCharacter character) 
	{
		super(game, manager, character, 2000);
		this.game = game;
	}

	@Override
	public void interrupt(){}

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
		game.removeCharacter(character);
		game.getQuestEngine().died(character);
		manager.nextAnimation();
	}

	@Override
	public void animation(double seconds)  {}
	
	private static final long serialVersionUID = -3895351315046806201L;
}
