package com.upl.mmorpg.lib.quest.action;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.quest.Quest;

public class CharacterKilled extends QuestAction
{
	public CharacterKilled(Game game, MMOCharacter attacker, MMOCharacter victim)
	{
		super(game);
		this.attacker = attacker;
		this.victim = victim;
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
				q.next().killed(attacker, victim);
		}
	}
	
	@Override
	public String description() 
	{
		return attacker.getName() + " has killed " + victim.getName() + ".";
	}
	
	private MMOCharacter attacker;
	private MMOCharacter victim;
}
