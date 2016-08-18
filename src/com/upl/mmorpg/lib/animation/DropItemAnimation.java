package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.lib.liblog.Log;

/**
 * Animation for picking up an item on the map. It is assumed that by
 * the time this animation is ready to start, the character is on the
 * square that contains the item.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */
public class DropItemAnimation extends Animation
{
	public DropItemAnimation(Game game, AnimationManager manager, MMOCharacter character, AnimationListener listener, Item item) 
	{
		super(game, manager, character, listener, 1);
		this.item = item;
	}

	@Override
	public void animationInterrupted(Animation animation) {}

	@Override
	public void animationStarted() 
	{
		if(!game.dropItem(character, item))
			Log.e("Failed to drop item!");
		manager.nextAnimation();
	}

	@Override
	public void animationReelFinished() {}

	@Override
	public void animation(double seconds) {}

	@Override
	public void directionChanged(int direction) {}
	
	private static final long serialVersionUID = -4243819479990411151L;
	
	private transient Item item;
}
