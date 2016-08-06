package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import com.upl.mmorpg.game.item.ItemList;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class MapSquare extends Renderable implements Serializable
{
	protected MapSquare(AssetManager assets)
	{
		this.assets = assets;
		destroyed = false;
		destructible = false;
		image_name = null;
		overlay_name = null;
		destroyed_overlay = null;
		overlay = null;
		passThrough = true;
		passThroughWhenDestroyed = true;
		
		isMapLink = false;
		linked_map = null;
		link_row = -1;
		link_col = -1;
	}
	
	public MapSquare(double x, double y, double size,
			AssetManager assets, String image_name, 
			String overlay_name, String destroyed_overlay_name)
	{
		this.locX = x;
		this.locY = y;
		this.width = size;
		this.height = size;
		this.assets = assets;
		this.image_name = image_name;
		this.overlay_name = overlay_name;
		this.destroyed_overlay_name = destroyed_overlay_name;

		destroyed = false;
		destructible = false;
		destroyed_overlay = null;
		overlay = null;
		passThrough = true;
		passThroughWhenDestroyed = true;
		
		isMapLink = false;
		linked_map = null;
		link_row = -1;
		link_col = -1;
	}

	@Override
	public void loadImages() throws IOException
	{
		if(image_name != null)
			image = assets.loadImage(image_name);
		if(overlay_name != null)
			overlay = assets.loadImage(overlay_name);
		if(destroyed_overlay_name !=  null)
		{
			destructible = true;
			destroyed_overlay = assets.loadImage(destroyed_overlay_name);
		}
	}

	@Override
	public void render(Graphics2D g) 
	{
		if(image != null)
			g.drawImage(image, (int)locX, (int)locY, 
					(int)width, (int)height, null);

		if(overlay != null && !destroyed)
			g.drawImage(overlay, (int)locX, (int)locY, 
					(int)width, (int)height, null);

		if(destroyed && destroyed_overlay != null)
			g.drawImage(destroyed_overlay, (int)locX, (int)locY, 
					(int)width, (int)height, null);
	}

	@Override
	public String getRenderName() 
	{
		return "Map Square: " + image_name;
	}

	public void setDestroyed(boolean destroyed)
	{
		if(destructible)
			this.destroyed = destroyed;
		else this.destroyed = false;
	}

	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public boolean isLinkLanding()
	{
		return isLinkLanding;
	}
	
	public boolean isPassable()
	{
		return passThrough;
	}
	
	protected transient AssetManager assets;
	protected String image_name;
	protected String overlay_name;
	protected String destroyed_overlay_name;
	protected transient BufferedImage image;
	protected transient BufferedImage overlay;
	protected transient BufferedImage destroyed_overlay;
	protected boolean destroyed;
	protected boolean destructible;
	protected boolean passThrough;
	protected boolean passThroughWhenDestroyed;
	protected boolean isLinkLanding;
	protected boolean isMapLink;
	protected String linked_map;
	protected int link_row;
	protected int link_col;
	protected ItemList stack;
	
	private static final long serialVersionUID = -5877817070442524231L;
}
