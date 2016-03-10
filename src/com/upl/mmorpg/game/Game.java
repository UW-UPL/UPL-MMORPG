package com.upl.mmorpg.game;

import java.io.IOException;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Game 
{
	public Game(String map_path, AssetManager assets, 
			boolean headless, boolean fps, boolean vsync) 
	{
		this.assets = assets;
		this.map_path = map_path;
		this.headless = headless;
		render = new RenderPanel(vsync, fps, headless);
		characters = new LinkedList<MMOCharacter>();
	}
	
	public void loadMap() throws IOException
	{
		if(map == null) return;
		map.load(map_path, assets, TILE_SIZE);
		map.loadAllImages();
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
	
	protected RenderPanel render;
	protected AssetManager assets;
	protected Grid2DMap map;
	protected String map_path;
	protected boolean headless;
	protected LinkedList<MMOCharacter> characters;
	
	protected static final double TILE_SIZE = 64;
}
