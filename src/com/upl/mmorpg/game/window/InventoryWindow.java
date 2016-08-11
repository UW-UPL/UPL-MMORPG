package com.upl.mmorpg.game.window;

import java.io.IOException;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.gui.AssetManager;

public class InventoryWindow extends Window{

	public InventoryWindow(double x, double y, AssetManager assets, Game game) {
		super(x, y, assets, game);
		// TODO Auto-generated constructor stub
	}
	
	public static void prefetchAssets(AssetManager assets, Game game) 
			throws IOException
	{
		InventoryWindow w = new InventoryWindow(0, 0, assets, game);
	}

}
