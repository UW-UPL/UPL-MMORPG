package com.upl.mmorpg.game.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
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
			if(!this.loadMap())
				Log.e("MAP UPDATE FAILED.");
			if(!this.loadCharacters())
				Log.e("CHARACTER UPDATE FAILED.");
		} catch(Exception e)
		{
			Log.wtf("Client Initialization Failed!!", e);
		}
	}

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
		render.removeAllRenderables();
		render.addRenderable(currentMap);
		
		return true;
	}

	public boolean loadCharacters() throws IOException
	{
		Object obj = gameState.requestCharacters();
		@SuppressWarnings("unchecked")
		LinkedList<MMOCharacter> characters = (LinkedList<MMOCharacter>)obj;
		if(characters == null)
			return false;
		currentMap.setCharacters(characters);
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
	
	public boolean updateCharacter(MMOCharacter character, int entity_id)
	{
		Iterator<MMOCharacter> it = currentMap.getCharacters().iterator();
		while(it.hasNext())
		{
			MMOCharacter c = it.next();
			if(c.getEntityId() == character.getEntityId())
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
		
		return false;
	}

	private ClientGameStateManager gameState;
	private JFrame window;
	private MapControl control;
	private RPCManager rpc;
	private Grid2DMap currentMap;
}
