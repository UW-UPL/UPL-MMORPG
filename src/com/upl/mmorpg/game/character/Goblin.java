package com.upl.mmorpg.game.character;

import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Goblin extends NonPlayerCharacter
{
	public Goblin(int row, int col,
			Grid2DMap map, AssetManager assets) 
	{
		super(row, col, map, assets);
		
		try 
		{
			setAnimationReels("assets/models/goblin");
		} catch (IOException e) 
		{
			Log.e("Couldn't find character reels: Goblin");
		}
		
		this.idle();
	}
	
	public static void prefetchAssets(AssetManager assets, double tile_size) 
			throws IOException
	{
		Grid2DMap map = new Grid2DMap(tile_size);
		Goblin g = new Goblin(0, 0, map, assets);
		g.setAnimationReels("assets/models/goblin");
	}

	@Override
	public String getRenderName() 
	{
		return "Goblin";
	}
}
