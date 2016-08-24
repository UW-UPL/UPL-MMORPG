package com.upl.mmorpg.lib.quest;

import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.lib.quest.action.CharacterDamaged;
import com.upl.mmorpg.lib.quest.action.CharacterDied;
import com.upl.mmorpg.lib.quest.action.CharacterKilled;
import com.upl.mmorpg.lib.quest.action.CharacterMovedTo;
import com.upl.mmorpg.lib.quest.action.DroppedItem;
import com.upl.mmorpg.lib.quest.action.PickedUpItem;
import com.upl.mmorpg.lib.quest.action.QuestAction;

public class QuestEngine
{
	public QuestEngine(Game game)
	{
		this.game = game;
		executionQueue = new LinkedList<QuestAction>();
	}
	
	public void died(MMOCharacter character)
	{
		addQuest(new CharacterDied(game, character));
	}
	
	public void killed(MMOCharacter attacker, MMOCharacter victim)
	{
		addQuest(new CharacterKilled(game, attacker, victim));
	}
	
	public void damaged(MMOCharacter attacker, MMOCharacter victim, int damage)
	{
		addQuest(new CharacterDamaged(game, attacker, victim, damage));
	}
	
	public void movedTo(MMOCharacter character, int row, int col)
	{
		addQuest(new CharacterMovedTo(game, character, row, col));
	}
	
	public void pickedUp(MMOCharacter character, Item item)
	{
		addQuest(new PickedUpItem(game, character, item));
	}
	
	public void dropped(MMOCharacter character, Item item)
	{
		addQuest(new DroppedItem(game, character, item));
	}
	
	private void addQuest(QuestAction quest)
	{
		synchronized(executionQueue)
		{
			executionQueue.add(quest);
		}
	}
	
	public void quest_execution_scheduler()
	{
		while(running)
		{
			try { Thread.sleep(QUEST_SCHEDULER_WAIT); } catch(Exception e) {}
			if(!running) break;
			
			synchronized(executionQueue)
			{
				Iterator<QuestAction> q = executionQueue.iterator();
				while(q.hasNext())
				{
					QuestAction quest = q.next();
					System.out.println("QUEST: " + quest.description());
					quest.execute();
					q.remove();
				}
			}
		}
	}
	
	public void startScheduler()
	{
		if(running) return;
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				quest_execution_scheduler();
			}
		};
		
		running = true;
		thread = new Thread(run);
		thread.start();
	}
	
	public void stopScheduler()
	{
		running = false;
		try { thread.interrupt(); } catch(Exception e) {}
		try { thread.join(1000); } catch(Exception e) {}
		thread = null;
	}
	
	private Game game;
	private LinkedList<QuestAction> executionQueue;
	private boolean running;
	private Thread thread;
	
	private static final int QUEST_SCHEDULER_WAIT = 250;
}
