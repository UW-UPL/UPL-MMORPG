package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.uuid.ItemUUID;
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
	public DropItemAnimation(Game game, AnimationManager manager, MMOCharacter character, Item item) 
	{
		super(game, manager, character, 1);
		this.item = item.getUUID();
	}

	@Override
	public void interrupt(){}

	@Override
	public void animationStarted() 
	{
		Item i = game.getItem(item);
		if(i == null)
		{
			Log.e("Where is item (DropItemAnimation): " + item);
			return;
		}
		
		if(!game.dropItem(character, i))
			Log.e("Failed to drop item!");
		manager.nextAnimation();
	}

	@Override public void animationReelFinished() {}
	@Override public void animation(double seconds) {}
	@Override public void animationStopped() {}
	
	@Override
	public String toString()
	{
		return "Drop Item Animation";
	}
	
	private ItemUUID item; /**< The UUID of the item that is being dropped. */

	private static final long serialVersionUID = -4243819479990411151L;
}
