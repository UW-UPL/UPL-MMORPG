package com.upl.mmorpg.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.item.ItemList;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.map.MapSquare;
import com.upl.mmorpg.lib.quest.QuestEngine;

public class Game 
{
	public Game(AssetManager assets, boolean headless, boolean fps, boolean vsync) 
	{
		this.assets = assets;
		this.headless = headless;
		questEngine = new QuestEngine(this);
		questEngine.startScheduler();
		render = new RenderPanel(vsync, fps, headless);
		characters = new LinkedList<MMOCharacter>();
		maps = new Grid2DMap[map_paths.length];
		for(int x = 0;x < maps.length;x++)
			maps[x] = new Grid2DMap(TILE_SIZE, x);
	}

	public void loadAssets() throws IOException
	{
		/* Supported Characters */
		Goblin.prefetchAssets(assets, this);
	}

	public void loadMaps() throws IOException
	{
		if(maps == null) return;

		for(int x = 0;x < maps.length;x++)
		{
			if(!maps[x].load(map_paths[x], assets, TILE_SIZE))
				throw new IOException("Map format exception");
			maps[x].loadAllImages(assets);
		}
	}

	public void addCharacter(MMOCharacter c)
	{
		characters.add(c);
		render.addRenderable(c);
	}

	public void removeCharacter(MMOCharacter c)
	{
		characters.remove(c);
		render.removeRenderable(c);
	}

	public ItemList getItemsOnSquare(int row, int col, int map_id)
	{
		MapSquare square = maps[map_id].getSquare(row, col);
		if(square == null)
			return null;
		return square.getItems();
	}

	public synchronized boolean pickupItem(MMOCharacter character, Item item, int map_id)
	{
		int row = character.getRow();
		int col = character.getCol();

		/* Get the square the character is currently on */
		MapSquare square = maps[map_id].getSquare(row, col);

		/* Are they on a valid square? */
		if(square == null)
			return false;

		/* Is this item on the square? */
		if(square.getItems().containsItem(item))
		{
			/* Does this character have room for this item? */
			if(character.receiveItem(item))
			{
				square.getItems().removeItem(item);
				/* Let everyone know someone picked up an item */
				questEngine.pickedUp(character, item);
				return true;
			}
		}

		return false;
	}

	public Goblin createGoblin(int row, int col, int map_id)
	{
		Goblin g = new Goblin(row, col, maps[map_id], assets, this);
		this.addCharacter(g);
		return g;
	}

	public Iterator<MMOCharacter> characterIterator()
	{
		return characters.iterator();
	}

	public QuestEngine getQuestEngine()
	{
		return questEngine;
	}
	
	protected RenderPanel render;
	protected AssetManager assets;
	protected QuestEngine questEngine;
	protected boolean headless;
	protected LinkedList<MMOCharacter> characters;
	
	protected Grid2DMap maps[];
	protected String map_paths[] = {
		"assets/maps/example.mmomap"	
	};
	
	private static final int TILE_SIZE = 32;
}
