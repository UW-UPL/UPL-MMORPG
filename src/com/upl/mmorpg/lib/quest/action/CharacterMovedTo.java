package com.upl.mmorpg.lib.quest.action;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.quest.Quest;

public class CharacterMovedTo extends QuestAction
{
	public CharacterMovedTo(Game game, MMOCharacter character, int row, int col)
	{
		super(game);
		this.character = character;
		this.row = row;
		this.col = col;
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
				q.next().movedTo(character, row, col);
		}
	}
	
	public String description()
	{
		return character.getName() + " has moved to " 
				+ row + "," + col + ".";
	}
	
	private MMOCharacter character;
	private int row;
	private int col;
}
