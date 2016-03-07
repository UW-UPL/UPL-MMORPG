package com.upl.mmorpg.game;

import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Game 
{
	public Game(String map_path, AssetManager assets) throws IOException
	{
		this.assets = assets;
		render = new RenderPanel(true, false, true);
		map = new Grid2DMap();
		map.load(map_path, assets, TILE_SIZE);
		map.loadAllImages();
		render.addBPRenderable(map);
		render.startRender();
	}
	
	private RenderPanel render;
	private AssetManager assets;
	private Grid2DMap map;
	
	private static final int TILE_SIZE = 32;
	
	public static void main(String args[])
	{
		try 
		{
			new Game("assets/maps/example-map1.mmomap", new AssetManager());
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
