package com.upl.mmorpg.game.client;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
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
		
		control = new MapControl(render, maps[0]);
		render.addMouseListener(control);
		render.addMouseMotionListener(control);
		
		gameState = new ClientGameStateManager(this, rpc);
		this.updateMap();
	}
	
	private void updateMap()
	{
		currentMap = (Grid2DMap)gameState.requestCurrentMap();
		if(currentMap == null)
			Log.e("NETWORK MAP ERROR.\n");
		render.removeAllBPRenderable();
		render.addRenderable(currentMap);
	}
	
	private ClientGameStateManager gameState;
	private JFrame window;
	private MapControl control;
	private RPCManager rpc;
	private Grid2DMap currentMap;
}
