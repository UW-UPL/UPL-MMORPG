package com.upl.mmorpg.lib.map.edit;

import java.awt.Color;
import java.awt.Graphics2D;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.map.MapSquare;

public class EditableMapSquare extends MapSquare 
{
	private EditableMapSquare(AssetManager assets){super(assets);}
	public EditableMapSquare(double x, double y, double size,
			AssetManager assets, String image_name, String overlay_name,
			String destroyed_overlay_name) 
	{
		super(x, y, size, assets, image_name, overlay_name, destroyed_overlay_name);
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		super.render(g);
		if(!this.passThrough)
		{
			g.setColor(Color.BLUE);
			g.drawRect((int)locX, (int)locY, (int)width - 1, (int)height - 1);
			g.drawRect((int)locX + 1, (int)locY + 1, (int)width - 3, (int)height - 3);
		}
		
		if(this.destructible)
		{
			g.setColor(Color.RED);
			g.drawRect((int)locX + 2, (int)locY + 2, (int)width - 5, (int)height - 5);
			g.drawRect((int)locX + 3, (int)locY + 3, (int)width - 7, (int)height - 7);
		}
	}

	public void setImage(String image_name)
	{
		this.image_name = image_name;
	}
	
	public void setOverlay(String overlay_name)
	{
		this.overlay_name = overlay_name;
	}
	
	public void setDestroyedOverlay(String destroyed_overlay_name)
	{
		this.destroyed_overlay_name = destroyed_overlay_name;
	}
	
	public void setPassThrough(boolean passThrough)
	{
		this.passThrough = passThrough;
	}
	
	public synchronized boolean isPassThrough()
	{
		if(isDestroyed())
		{
			return passThroughWhenDestroyed;
		} else return this.passThrough;
	}
	
	public void setDestructible(boolean destructible)
	{
		this.destructible = destructible;
	}
	
	public static EditableMapSquare import_square(String line, AssetManager assets, 
			double x, double y, double size)
	{
		String parts[] = line.split(",");
		EditableMapSquare out = new EditableMapSquare(assets);
		out.image_name = parts[0];
		out.overlay_name = parts[1];
		out.destroyed_overlay_name = parts[2];
		out.destructible = parts[3].equalsIgnoreCase("true");
		out.passThrough = parts[4].equalsIgnoreCase("true");
		out.passThroughWhenDestroyed = parts[5].equalsIgnoreCase("true");
		
		if(out.image_name.equalsIgnoreCase("null"))
			out.image_name = null;
		if(out.overlay_name.equalsIgnoreCase("null"))
			out.overlay_name = null;
		if(out.destroyed_overlay_name.equalsIgnoreCase("null"))
			out.destroyed_overlay_name = null;
		
		out.locX = x;
		out.locY = y;
		out.width = size;
		out.height = size;
		
		return out;
	}
}
