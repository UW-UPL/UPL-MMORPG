package com.upl.mmorpg.game.character;

import java.io.IOException;

import com.upl.mmorpg.lib.animation.IdleAnimation;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class Goblin extends MMOCharacter
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
	}

	@Override
	public String getRenderName() 
	{
		return "Goblin";
	}
}
