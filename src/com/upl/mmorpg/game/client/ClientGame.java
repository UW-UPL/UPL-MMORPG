package com.upl.mmorpg.game.client;

import java.io.IOException;
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
		try
		{
			if(!this.updateMap())
				Log.e("MAP UPDATE FAILED.");
			if(!this.updateCharacters())
				Log.e("CHARACTER UPDATE FAILED.");
		} catch(Exception e)
		{
			Log.wtf("Client Initialization Failed!!", e);
		}
	}

	private boolean updateMap() throws IOException
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

	public boolean updateCharacters()
	{
		Object obj = gameState.requestCharacters();
		@SuppressWarnings("unchecked")
		LinkedList<MMOCharacter> characters = (LinkedList<MMOCharacter>)obj;
		if(characters == null)
			return false;
		currentMap.setCharacters(characters);
		System.out.println("Got " + characters.size() + " characters.");
		
		return true;
	}

	private ClientGameStateManager gameState;
	private JFrame window;
	private MapControl control;
	private RPCManager rpc;
	private Grid2DMap currentMap;
}
