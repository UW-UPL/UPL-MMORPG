package com.upl.mmorpg.game.character;

import java.io.IOException;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Goblin extends NonPlayerCharacter
{
	public Goblin(int row, int col, Grid2DMap map, AssetManager assets, Game game) 
	{
		super(row, col, map, assets, game);
		
		try 
		{
			setAnimationReels("assets/models/goblin");
		} catch (IOException e) 
		{
			Log.e("Couldn't find character reels: Goblin");
		}
		
		this.idle();
		
		this.walkingSpeed = 1.0d;
		this.maxHealth = 10;
		this.health = 10;
		this.attackSpeed = 3.0d;
	}
	
	public static void prefetchAssets(AssetManager assets, double tile_size, Game game) 
			throws IOException
	{
		Grid2DMap map = new Grid2DMap(tile_size);
		Goblin g = new Goblin(0, 0, map, assets, game);
		g.setAnimationReels("assets/models/goblin");
	}

	@Override
	public String getRenderName() 
	{
		return "Goblin";
	}
}
