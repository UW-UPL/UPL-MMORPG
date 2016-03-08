package com.upl.mmorpg.game.character;

import java.awt.Graphics2D;

import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class MMOCharacter extends Renderable
{
	public MMOCharacter(double x, double y, double width, double height, 
			Grid2DMap map)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.map = map;
	}

	public void walkTo(int row, int col)
	{
		
	}
	
	@Override public abstract void render(Graphics2D g);
	@Override public abstract String getRenderName();
	
	protected Grid2DMap map;
}
