package com.upl.mmorpg.game.character;

import java.io.IOException;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Goblin extends NonPlayerCharacter
{
	public Goblin(int row, int column, Grid2DMap map, AssetManager assets, Game game) 
	{
		super(0, 0, 0, 0, map, assets, game);
		this.setGridPosition(row, column);
		
		try 
		{
			setAnimationReels("assets/models/goblin");
		} catch (IOException e) 
		{
			Log.e("Couldn't find character reels: Goblin");
		}
		
		this.idle();
		
		this.name = "Goblin";
		this.walkingSpeed = 1.0d;
		this.maxHealth = 10;
		this.health = 10;
		this.attackSpeed = 3.0d;
	}
	
	public static void prefetchAssets(AssetManager assets, Game game) 
			throws IOException
	{
	}

	@Override
	public String getRenderName() 
	{
		return "Goblin";
	}
	
	private static final long serialVersionUID = 1203515561216565986L;
}
