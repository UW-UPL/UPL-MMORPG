package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class AttackAnimation extends FollowAnimation
{
	public AttackAnimation(Game game, AnimationManager manager, MMOCharacter character, Grid2DMap map) 
	{
		super(game, manager, character, map, 1);
		animating = false;
		lastAttack = 0.0d;
	}
	
	public void setAttacking(MMOCharacter attacking)
	{
		this.attacking = attacking;
		// this.setFollee(attacking);
	}
	
	public void interruptAttack()
	{
		animating = false;
	}
	
	@Override
	public void interrupt()
	{
		animating = false;
	}

	@Override
	public void animationStarted() 
	{
		super.animationStarted();
		animating = true;
		lastAttack = 0.0d;
		//idle.animationStarted();
	}

	@Override
	public void animationReelFinished() 
	{
		super.animationReelFinished();
		//idle.animationStarted();
	}

	@Override
	public void animation(double seconds) 
	{
		super.animation(seconds);
		if(!animating) return;
		
		lastAttack += seconds;
		if(lastAttack >= character.getAttackSpeed())
		{
			/* Punch again */
			lastAttack = 0;
			attacking.takeDamage(2, character);
			attack_animation();
		}
	}
	
	protected abstract void attack_animation();
	
	@Override
	public String toString()
	{
		return "Attack animation";
	}
	
	private boolean animating;
	private double lastAttack;
	private transient MMOCharacter attacking;
	
	private static final long serialVersionUID = -1742965394212302361L;
}
