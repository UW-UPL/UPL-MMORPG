package com.upl.mmorpg.lib.quest.action;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.quest.Quest;

public class CharacterDamaged extends QuestAction
{
	public CharacterDamaged(Game game, MMOCharacter attacker, MMOCharacter victim, 
			int damage)
	{
		super(game);
		this.attacker = attacker;
		this.victim = victim;
		this.damage = damage;
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
				q.next().damaged(attacker, victim, damage);
		}
	}
	
	@Override
	public String description()
	{
		return attacker.getName() + " dealt " + damage + " to " + victim.getName() + ".";
	}
	
	private MMOCharacter attacker;
	private MMOCharacter victim;
	private int damage;
}
