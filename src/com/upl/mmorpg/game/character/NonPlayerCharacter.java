package com.upl.mmorpg.game.character;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.animation.WanderAnimation;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class NonPlayerCharacter extends MMOCharacter 
{
	public NonPlayerCharacter(double x, double y, double width, double height,
			Grid2DMap map, AssetManager assets, Game game) 
	{
		super(x, y, width, height, map, assets, game);
		wander = new WanderAnimation(animation, this, map, 
				map.getTileSize(), null);
	}

	public NonPlayerCharacter(int row, int col, Grid2DMap map,
			AssetManager assets, Game game) 
	{
		super(row, col, map, assets, game);
		wander = new WanderAnimation(animation, this, map, 
				map.getTileSize(), null);
	}

	@Override
	public abstract String getRenderName();

	public void wander(int radius)
	{
		wander.setRadius(radius);
		animation.setAnimation(wander);
	}
	
	private WanderAnimation wander;
}
