package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.item.ItemList;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class MapSquare extends Renderable implements Serializable
{
	protected MapSquare()
	{
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
	
	public MapSquare(double x, double y, double size, String image_name, 
			String overlay_name, String destroyed_overlay_name)
	{
		this.locX = x;
		this.locY = y;
		this.width = size;
		this.height = size;
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
	public void loadImages(AssetManager assets) throws IOException
	{
		if(image_name != null)
		{
			image = assets.loadImage(image_name);
			if(image == null) throw new IOException(image_name + " not found!\n");
		}
		
		if(overlay_name != null)
		{
			overlay = assets.loadImage(overlay_name);
			if(overlay == null) throw new IOException(overlay_name + " not found!\n");
		}
		
		if(destroyed_overlay_name !=  null)
		{
			destructible = true;
			destroyed_overlay = assets.loadImage(destroyed_overlay_name);
			if(destroyed_overlay == null)
				throw new IOException(destroyed_overlay + " not found!\n");
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
	
	/**
	 * Called when an item is dropped on this space. This could
	 * be an item or an item stack.
	 * @param item The item that was dropped.
	 */
	public void itemDropped(Item item)
	{
		stack.addItem(item);
	}
	
	/**
	 * Get all of the items on the MapSquare.
	 * @return The list of items on the MapSquare.
	 */
	public ItemList getItems()
	{
		return stack;
	}
	
	protected String image_name; /**< The asset name of the image for this MapSquare. */
	protected String overlay_name; /**< If the asset has an overlay, this is the overlay asset. */
	protected String destroyed_overlay_name; /**< If this square is destroyed, this is the overlay asset. */
	protected transient BufferedImage image; /**< Background image asset for the MapSquare. */
	protected transient BufferedImage overlay; /**< Overlay image. This must have an alpha channel. */
	protected transient BufferedImage destroyed_overlay; /**< The overlay to be drawn when the square is destroyed. */
	protected boolean destroyed; /**< Whether or not this square is destroyed. */
	protected boolean destructible; /**< Whether or not this square can be destroyed. */
	protected boolean passThrough; /**< Whether or not this square can be passed through. */
	protected boolean passThroughWhenDestroyed; /**< Whether or not this square can be passed through when destroyed. */
	protected boolean isLinkLanding; /**< Whether or not this space can be traveled to. */
	protected boolean isMapLink; /**< Whether or not this square can move a player */
	protected String linked_map; /**< The map the map link goes to. */
	protected int link_row; /**< The row the link takes the player to. */
	protected int link_col; /**< The column the link takes the player to. */
	protected ItemList stack; /**< All of the items on the square. */
	
	private static final long serialVersionUID = -5877817070442524231L;
}
