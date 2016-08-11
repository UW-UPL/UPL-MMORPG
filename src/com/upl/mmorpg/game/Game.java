package com.upl.mmorpg.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.window.InventoryWindow;
import com.upl.mmorpg.game.window.Window;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.quest.QuestEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Game
{
	public Game(String map_path, AssetManager assets, 
			boolean headless, boolean fps, boolean vsync) 
	{
		this.assets = assets;
		this.map_path = map_path;
		this.headless = headless;
		questEngine = new QuestEngine(this);
		questEngine.startScheduler();
		render = new RenderPanel(vsync, fps, headless);
		characters = new LinkedList<MMOCharacter>();
		windows = new LinkedList<Window>();
	}
	
	public void loadAssets() throws IOException
	{
		/* Supported Characters */
		Goblin.prefetchAssets(assets, TILE_SIZE, this);
		
		/* Supported Windows */
		InventoryWindow.prefetchAssets(assets, this);
	}
	
	public void loadMap() throws IOException
	{
		if(map == null) return;
		if(!map.load(map_path, assets, TILE_SIZE))
			throw new IOException("Map format exception");
		map.loadAllImages(assets);
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
	
	public void addWindow(Window w){
		windows.add(w);
		render.addGPRenderable(w);
	}
	
	public void removeWindow(Window w){
		windows.remove(w);
		render.removeRenderable(w);
	}
	
	public Goblin createGoblin(int row, int col)
	{
		Goblin g = new Goblin(row, col, map, assets, this);
		this.addCharacter(g);
		return g;
	}
	
	public void createWindows(){
		InventoryWindow i = new InventoryWindow(0, 0, assets, this);
		this.addWindow(i);
	}
	
	public Iterator<MMOCharacter> characterIterator()
	{
		return characters.iterator();
	}
	
	public QuestEngine getQuestEngine()
	{
		return questEngine;
	}
	
	private void keyPressed(KeyEvent e){
		
		if(e.getKeyCode()== KeyEvent.VK_ESCAPE){
			
		}
	}
	
	protected RenderPanel render;
	protected AssetManager assets;
	protected QuestEngine questEngine;
	protected Grid2DMap map;
	protected String map_path;
	protected boolean headless;
	protected LinkedList<MMOCharacter> characters;
	protected LinkedList<Window> windows;
	
	protected static final double TILE_SIZE = 40;

}
