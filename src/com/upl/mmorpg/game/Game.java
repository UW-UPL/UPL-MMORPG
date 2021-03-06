package com.upl.mmorpg.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.item.ItemList;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.map.MapSquare;
import com.upl.mmorpg.lib.quest.QuestEngine;

public abstract class Game 
{
	/**
	 * Create a new UPL-MMORPG game.
	 * @param assets The asset manager to use.
	 * @param headless Whether or not a render window will be used.
	 * @param fps Whether or not to display frames per second (non headless only).
	 * @param vsync Whether or not to sync the framerate to the screen (non headless only).
	 */
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
			maps[x] = new Grid2DMap(x);
	}

	/**
	 * Load all of the game assets we expect to use.
	 * @throws IOException If the assets cannot be found.
	 */
	public void loadAssets() throws IOException
	{
		/* Supported Characters */
		Goblin.prefetchAssets(assets, this);
	}
	
	/**
	 * Returns the asset manager for this game.
	 * @return The asset manager for this game.
	 */
	public AssetManager getAssetManager()
	{
		return assets;
	}

	/**
	 * Load all of the maps as assets.
	 * @throws IOException If one or more maps cannot be found.
	 */
	public void loadMaps() throws IOException
	{
		for(int x = 0;x < getMapCount();x++)
		{
			if(!getMap(x).load(map_paths[x], assets))
				throw new IOException("Map format exception");
			getMap(x).loadAllImages(assets);
		}
	}

	/**
	 * Add a new character to a map.
	 * @param c The character to add to the map.
	 * @param map_id The id of the map in which to add the character.
	 * @return Whether or not the character could be added to the map.
	 */
	public boolean addCharacter(MMOCharacter c, int map_id)
	{
		if(map_id >= 0 && map_id < getMapCount())
		{
			characters.add(c);
			render.addRenderable(c);
			getMap(map_id).addCharacter(c);
			c.setCurrentMap(getMap(map_id));

			return true;
		}

		return false;
	}

	/**
	 * Remove a character from the game.
	 * @param c The character to remove.
	 */
	public void removeCharacter(MMOCharacter c)
	{
		if(!headless)
			render.removeRenderable(c);

		/* remove the character from their map. */
		characters.remove(c);
		c.getCurrentMap().removeCharacter(c);
		c.setCurrentMap(null);
	}

	/**
	 * Convert an ItemUUID into the item it represents.
	 * @param uuid The UUID to search for.
	 * @return The item, if it exists, null otherwise.
	 */
	public Item getItem(ItemUUID uuid)
	{
		Item i = getItemOnMap(uuid);
		if(i != null) return i;

		/* Does a character have this item? */
		Iterator<MMOCharacter> it = characters.iterator();
		while(it.hasNext())
		{
			MMOCharacter character = it.next();
			Item item = character.getInventory().containsUUID(uuid);

			if(item != null)
			{
				Log.vln("Character " + character + " had the item we were searching for.");
				return item;
			}
		}

		return null;
	}
	
	/**
	 * Returns the list of items that are on a given square.
	 * @param row The row of the square.
	 * @param col The column of the square.
	 * @param map_id The id of the map to search on.
	 * @return The list of items on that square, if the square is not null.
	 */
	public ItemList getItemsOnSquare(int row, int col, int map_id)
	{
		MapSquare square = getMap(map_id).getSquare(row, col);
		if(square == null)
			return null;
		return square.getItems();
	}
	
	/**
	 * Search only maps for the item.
	 * @param uuid The UUID of the item to search for.
	 * @return The item, if it is on the map, null otherwise.
	 */
	public Item getItemOnMap(ItemUUID uuid)
	{
		for(int x = 0;x < getMapCount();x++)
		{
			Grid2DMap map = getMap(x);
			Iterator<Item> item_it = map.getItems().iterator();

			while(item_it.hasNext())
			{
				Item i = item_it.next();
				if(i.getUUID().equals(uuid))
					return i;
			}
		}

		return null;
	}

	/**
	 * Pickup an item from the map.
	 * @param character The character to receive the item.
	 * @param item The item within the square the character is standing on.
	 * @return Whether or not the item could be picked up.
	 */
	public synchronized boolean pickupItem(MMOCharacter character, Item item)
	{
		int map_id = character.getCurrentMapID();
		int row = character.getRow();
		int col = character.getColumn();

		/* Get the square the character is currently on */
		MapSquare square = getMap(map_id).getSquare(row, col);

		/* Are they on a valid square? */
		if(square == null)
			return false;

		/* Is this item on the square? */
		if(square.getItems().contains(item))
		{
			/* Does this character have room for this item? */
			if(character.receiveItem(item))
			{
				square.getItems().remove(item);
				/* Let everyone know someone picked up an item */
				questEngine.pickedUp(character, item);
				return true;
			} else Log.e("Character's inventory is full!");
		} else Log.e("Character isn't on that square!");

		return false;
	}

	/**
	 * Drop an item onto the map.
	 * @param character The character that is dropping the item.
	 * @param item The item to drop on the square.
	 * @return Whether or not the item could be picked up.
	 */
	public synchronized boolean dropItem(MMOCharacter character, Item item)
	{
		Grid2DMap map = getMap(character.getCurrentMapID());
		int row = character.getRow();
		int col = character.getColumn();

		/* Get the square the character is currently on */
		MapSquare square = map.getSquare(row, col);

		/* Are they on a valid square? */
		if(square == null)
		{
			Log.e("Character is on invalid square!");
			return false;
		}

		/* Can we drop this item? */
		if(character.dropItem(item))
		{
			if(character.getCurrentMap().itemDropped(row, col, item))
			{
				/* Let everyone know someone dropped an item */
				questEngine.dropped(character, item);
			} else Log.e("Map square couldn't accept the dropped item!");
			return true;
		} else Log.e("Character couldn't drop that item!");

		return false;
	}

	/**
	 * Convert a CharacterUUID into the character it represents.
	 * @param uuid The UUID of the character to search for.
	 * @return The MMOCharacter, if it exists, null otherwise.
	 */
	public MMOCharacter getCharacter(CharacterUUID uuid)
	{
		Iterator<MMOCharacter> it = characters.iterator();
		while(it.hasNext())
		{
			MMOCharacter c = it.next();
			if(c.getUUID().equals(uuid))
				return c;
		}

		return null;
	}

	/**
	 * Returns an iterator for the list of characters in this game.
	 * @return An iterator for the list of characters in this game.
	 */
	public Iterator<MMOCharacter> characterIterator()
	{
		return characters.iterator();
	}
	
	/**
	 * Get all characters on a specific map.
	 * @param mapID The id of the map that contains the requested characters.
	 * @return The characters on the given map.
	 */
	public LinkedList<MMOCharacter> getCharactersOnMap(int mapID)
	{
		return getMap(mapID).getCharacters();
	}

	/**
	 * Returns the quest engine for this game.
	 * @return The quest engine for this game.
	 */
	public QuestEngine getQuestEngine()
	{
		return questEngine;
	}

	/**
	 * Returns the map object given a map id.
	 * @param id The id of the map.
	 * @return The map object.
	 */
	public Grid2DMap getMap(int id)
	{
		if(id >= 0 && id < maps.length)
			return maps[id];
		else return null;
	}

	/**
	 * Returns the amount of maps in the game.
	 * @return The amount of maps in the game.
	 */
	public int getMapCount()
	{
		return maps.length;
	}

	/**
	 * Put a goblin character onto the map/
	 * @param row The row in which to place the goblin.
	 * @param col The column in which to place the goblin.
	 * @param map_id The id of the map in which the goblin should be placed.
	 * @return The newly created goblin character.
	 */
	public Goblin createGoblin(int row, int col, int map_id)
	{
		Goblin g = new Goblin(row, col, getMap(map_id), this, CharacterUUID.generate());
		if(!addCharacter(g, map_id))
			Log.e("Failed to add goblin to map!!");
		return g;
	}

	/**
	 * Notify all of the players that a character has been updated.
	 * @param c The character that was updated.
	 * @param exclude Whether or not to exclude updating the character's owner.
	 */
	public abstract void characterUpdated(MMOCharacter c, boolean exclude);

	protected RenderPanel render; /**< The render panel used for rendering animation and graphics. */
	protected AssetManager assets; /**< The asset manager to use for loading assets. */
	protected QuestEngine questEngine; /**< This game's quest engine. */
	protected boolean headless; /**< Whether or not we are displaying graphics. */
	protected LinkedList<MMOCharacter> characters; /**< The characters on the map. */

	/** FITFALL NOTICE: do not touch maps[] directly, use getMap() and getMapCount(). */
	private Grid2DMap maps[]; /**< The currently loaded maps. */

	/* The list of maps to load when the game starts. */
	protected String map_paths[] = {
			"assets/maps/example.mmomap"	
	};
}
