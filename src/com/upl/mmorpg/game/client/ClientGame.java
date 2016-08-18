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
		character = null;

		//control = new MapControl(render, maps[0], false);
		//render.addMouseListener(control);
		//render.addMouseMotionListener(control);

		gameState = new ClientGameStateManager(this, rpc);
		rpc.setCallee(new ClientGameStateCalleeRPC(gameState));

		try
		{
			if(!loadMap())
				Log.e("MAP UPDATE FAILED.");
			if(!loadCharacters())
				Log.e("CHARACTER UPDATE FAILED.");
			render.startRender();
		} catch(Exception e)
		{
			Log.wtf("Client Initialization Failed!!", e);
		}

		Runnable run = new Runnable()
		{
			public void run()
			{
				Item i = ClientGame.this.currentMap.getItems().iterator().next();
				if(i == null)
				{
					Log.e("Where is this item??");
					return;
				}

				character.pickupItem(6, 6, i);
				character.addIdle(5000);
				character.addDropItem(6, 6, i);
				gameState.updateCharacter(character);
			}
		};
		new Thread(run).start();
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
		currentMap.updateTransient(0, true);
		currentMap.loadAllImages(assets);
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

		obj = gameState.requestPlayerUUID();
		if(obj instanceof CharacterUUID)
		{
			CharacterUUID uuid = (CharacterUUID)obj;
			character = getCharacter(uuid);
			if(character == null)
			{
				Log.e("Failed to locate our player: " + uuid);
				return false;
			}

			render.follow(character);
		} else Log.e("requestPlayerUUID return failure! " + obj);

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

	/**
	 * Assign the character we are in control of.
	 * @param character The character we are in control of.
	 */
	public void setCharacter(MMOCharacter character)
	{
		this.character = character;
	}

	/**
	 * Returns the character that we have control of.
	 * @return The character we have control of.
	 */
	public MMOCharacter getCharacter()
	{
		return character;
	}

	@Override
	public synchronized boolean pickupItem(MMOCharacter character, Item item) 
	{ 
		/* unless this is us, we don't care */
		if(character == this.character)
			gameState.requestPickUpItem(this.character.getRow(), this.character.getColumn(), item.getUUID());

		return true;
	}

	@Override
	public synchronized boolean dropItem(MMOCharacter character, Item item) 
	{ 
		/* unless this is us, we don't care */
		if(character == this.character)
			gameState.requestdropItem(character.getRow(), character.getColumn(), item.getUUID());

		return true; 
	}

	@Override public Grid2DMap getMap(int id){ return currentMap; }
	@Override public int getMapCount() { return 1; }
	
	@Override
	public void characterUpdated(MMOCharacter c, boolean exclude) {}

	private ClientGameStateManager gameState;
	private JFrame window;
	//private MapControl control;
	private Grid2DMap currentMap;
	private MMOCharacter character;
}
