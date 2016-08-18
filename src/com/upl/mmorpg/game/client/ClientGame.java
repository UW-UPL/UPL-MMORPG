package com.upl.mmorpg.game.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class ClientGame extends Game
{
	public ClientGame(AssetManager assets, RPCManager rpc)
	{
		super(assets, false, true, true);
		window = new JFrame("MMO Server Window");
		window.getContentPane().add(render);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);

		control = new MapControl(render, maps[0], false);
		render.addMouseListener(control);
		render.addMouseMotionListener(control);
		render.startRender();

		gameState = new ClientGameStateManager(this, rpc);
		rpc.setCallee(new ClientGameStateCalleeRPC(gameState));

		try
		{
			if(!loadMap())
				Log.e("MAP UPDATE FAILED.");
			if(!loadCharacters())
				Log.e("CHARACTER UPDATE FAILED.");
		} catch(Exception e)
		{
			Log.wtf("Client Initialization Failed!!", e);
		}
	}
	
	/**
	 * An item was dropped on the map.
	 * @param row The row in which the item was dropped.
	 * @param col The column in which the item was dropped.
	 * @param item The item that was dropped.
	 */
	public boolean itemDropped(int row, int col, Item item, MMOCharacter character)
	{
		/* Take the item away from the player */
		if(!character.getInventory().remove(item))
		{
			Log.e("Item couldn't be dropped by player " + character);
			return false;
		}
		
		/* Add the item to the map */
		currentMap.itemDropped(row, col, item);
		
		return true;
	}
	
	/**
	 * An item was picked up by a player.
	 * @param item The item that was picked up.
	 * @param recipient The character who picked up the item.
	 * @return Whether or not that character could pick up that item.s
	 */
	public boolean itemPickedUp(ItemUUID item, MMOCharacter recipient)
	{
		Item i = currentMap.pickupItem(item);
		if(i == null)
			return false;
		
		return recipient.getInventory().add(i);
	}

	/**
	 * Do the initial load of the map from the server.
	 * @return Whether or not the map could be loaded from the server.
	 * @throws IOException If there was a network error.
	 */
	private boolean loadMap() throws IOException
	{
		currentMap = (Grid2DMap)gameState.requestCurrentMap();
		currentMap.loadAllImages(assets);
		currentMap.setLoaded();
		currentMap.generateSquareProperties();
		System.out.println("ROWS: " + currentMap.getRows());
		System.out.println("COLUMNS: " + currentMap.getColumns());
		if(currentMap == null)
			return false;
		currentMap.findAllItems();
		System.out.println("This map contains " + currentMap.getItems().size() + " items.");
		render.removeAllRenderables();
		render.addRenderable(currentMap);

		return true;
	}

	/**
	 * Do the initial load of all of the characters on the map.
	 * @return Whether or not the characters could be loaded from the server.
	 * @throws IOException If there was a network failure.
	 */
	private boolean loadCharacters() throws IOException
	{
		Object obj = gameState.requestCharacters();
		@SuppressWarnings("unchecked")
		LinkedList<MMOCharacter> characters = (LinkedList<MMOCharacter>)obj;
		this.characters.addAll(characters);
		if(characters == null)
			return false;
		currentMap.setCharacters(characters);
		Log.vln("Client got " + characters.size() + " characters.");
		System.out.println("Got " + characters.size() + " characters.");
		Iterator<MMOCharacter> it = characters.iterator();
		while(it.hasNext())
		{
			MMOCharacter character = it.next();
			character.updateTransient(assets, this, currentMap);
			render.addRenderable(character);
		}

		return true;
	}
	
	/**
	 * Update the map properties. This includes items.
	 * @param map The new map properties.
	 * @return Whether or not the map could be updated.
	 */
	public boolean updateMap(Grid2DMap map)
	{
		return currentMap.update(map);
	}

	/**
	 * Update the properties of the given character.
	 * @param uuid The UUID of the character.
	 * @param character The new properties to apply.
	 * @return Whether or not this character could be updated.s
	 */
	public boolean updateCharacter(CharacterUUID uuid, MMOCharacter character)
	{
		Iterator<MMOCharacter> it = currentMap.getCharacters().iterator();
		while(it.hasNext())
		{
			MMOCharacter c = it.next();
			if(c.getUUID().equals(character.getUUID()))
			{
				try 
				{
					c.update(character);
				} catch (IOException e) 
				{
					Log.e("MMOCharacter update failed!");
					return false;
				}
				return true;
			}
		}
		
		/* This is a new character, just add it to the map */
		try 
		{
			character.updateTransient(assets, this, currentMap);
		} catch (IOException e) {
			Log.wtf("Couldn't update character transient!", e);
			return false;
		}
		
		characters.add(character);
		currentMap.addCharacter(character);
		render.addRenderable(character);

		return true;
	}
	
	/** Methods that are server specific that we want to cancel out */
	@Override
	public synchronized boolean pickupItem(MMOCharacter character, Item item) { return true; }

	private ClientGameStateManager gameState;
	private JFrame window;
	private MapControl control;
	private Grid2DMap currentMap;
}
