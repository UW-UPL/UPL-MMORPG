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
public class PickupItemAnimation extends Animation
{
	public PickupItemAnimation(Game game, AnimationManager manager, MMOCharacter character, Item item) 
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
			Log.e("Where is item (PickupItemAnimation): " + item);
			game.getMap(0).dumpItems();
			return;
		}
		
		if(!game.pickupItem(character, i))
			Log.e("Failed to pick up item!");
		manager.nextAnimation();
	}

	@Override
	public void animationReelFinished() {}

	@Override
	public void animation(double seconds) {}

	private static final long serialVersionUID = -4243819479990411151L;
	
	private ItemUUID item;
}
