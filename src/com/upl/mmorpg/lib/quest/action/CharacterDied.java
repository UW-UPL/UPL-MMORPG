package com.upl.mmorpg.lib.quest.action;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.quest.Quest;

public class CharacterDied extends QuestAction
{
	public CharacterDied(Game game, MMOCharacter character)
	{
		super(game);
		this.character = character;
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
				q.next().died(character);
		}
	}
	
	@Override
	public String description() 
	{
		return character.getName() + " has died.";
	}
	
	private MMOCharacter character;
}
