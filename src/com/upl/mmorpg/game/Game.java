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
	public Game(String map_path, AssetManager assets, 
			boolean headless, boolean fps, boolean vsync) 
	{
		this.assets = assets;
		this.map_path = map_path;
		this.headless = headless;
		questEngine = new QuestEngine(this);
		questEngine.startScheduler();
		render = new RenderPanel(vsync, fps, headless);
		characters = new LinkedList<MMOCharacter>();
	}
	
	public void loadAssets() throws IOException
	{
		/* Supported Characters */
		Goblin.prefetchAssets(assets, TILE_SIZE, this);
	}
	
	public void loadMap() throws IOException
	{
		if(map == null) return;
		if(!map.load(map_path, assets, TILE_SIZE))
			throw new IOException("Map format exception");
		map.loadAllImages(assets);
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
	
	public ItemList getItemsOnSquare(int row, int col)
	{
		MapSquare square = map.getSquare(row, col);
		if(square == null)
			return null;
		return square.getItems();
	}
	
	public synchronized boolean pickupItem(MMOCharacter character, Item item)
	{
		int row = character.getRow();
		int col = character.getCol();
		
		/* Get the square the character is currently on */
		MapSquare square = map.getSquare(row, col);
		
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
	
	public Goblin createGoblin(int row, int col)
	{
		Goblin g = new Goblin(row, col, map, assets, this);
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
	protected Grid2DMap map;
	protected String map_path;
	protected boolean headless;
	protected LinkedList<MMOCharacter> characters;
	
	protected static final double TILE_SIZE = 32;
}
