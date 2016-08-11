package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class PunchAnimation extends AttackAnimation
{
	public PunchAnimation(Game game, AnimationManager manager, 
			MMOCharacter character, Grid2DMap map, AnimationListener listener) 
	{
		super(game, manager, character, map, listener);
	}

	@Override
	protected void attack_animation()
	{
		if(!manager.setReel("punch", false))
			throw new RuntimeException("PUNCH ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(50);
	}
	
	@Override
	public void directionChanged(int direction) {}
	
	private static final long serialVersionUID = 5185740540745833021L;
}
