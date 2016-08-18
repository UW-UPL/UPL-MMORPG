package com.upl.mmorpg.lib.quest.action;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.lib.quest.Quest;

public class PickedUpItem extends QuestAction
{
	public PickedUpItem(Game game, MMOCharacter character, Item item)
	{
		super(game);
		this.character = character;
		this.item = item;
	}

	@Override
	public void execute() 
	{
		Iterator<MMOCharacter> it = game.characterIterator();
		while(it.hasNext())
		{
			MMOCharacter c = it.next();
			Iterator<Quest> q = c.getQuestIterator();
			while(q.hasNext())
				q.next().pickedUpItem(character, item);
		}
	}
	
	@Override
	public String description()
	{
		return character.getName() + " picked up item " + item.getName() + ".";
	}
	
	private MMOCharacter character;
	private Item item;
}
