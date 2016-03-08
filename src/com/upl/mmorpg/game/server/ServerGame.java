package com.upl.mmorpg.game.server;

import java.io.IOException;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.MapControl;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class ServerGame extends Game
{
	public ServerGame(String map_path, AssetManager assets, boolean headless)
	{
		super(map_path, assets, headless, true, true);
		control = new MapControl(render, map);
		render.addMouseListener(control);
		render.addMouseMotionListener(control);
		
		map = new Grid2DMap(render, TILE_SIZE);
		if(!headless)
		{
			window = new JFrame("MMO Server Window");
			window.getContentPane().add(render);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setResizable(false);
			window.setVisible(true);
		}
		
		render.startRender();
	}
	
	@Override
	public void loadMap() throws IOException
	{
		super.loadMap();
		if(!headless)
			render.addBPRenderable(map);
	}

	private JFrame window;
	private MapControl control;
	
	public static void main(String[] args) 
	{
		try {
			new ServerGame("assets/maps/example-map1.mmomap", 
					new AssetManager(), false).loadMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
