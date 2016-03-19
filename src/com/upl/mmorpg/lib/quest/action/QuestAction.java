package com.upl.mmorpg.lib.quest.action;

import com.upl.mmorpg.game.Game;

public abstract class QuestAction 
{
	public QuestAction(Game game)
	{
		this.game = game;
	}
	
	public abstract void execute();
	public abstract String description();
	
	protected Game game;
}
