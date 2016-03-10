package com.upl.mmorpg.game.server;

import java.io.IOException;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.Goblin;
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
	
	public void addGoblin(int row, int col)
	{
		this.addCharacter(new Goblin(row, col, map, assets));
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
			ServerGame g = new ServerGame("assets/maps/example-map1.mmomap", 
					new AssetManager(), false);
			g.loadMap();
			g.addGoblin(11, 11);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
