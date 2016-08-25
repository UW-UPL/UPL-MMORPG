package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.item.ItemList;
import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;

public class MapSquare extends Renderable implements Serializable
{
	public MapSquare(int row, int col, String image_name, 
			String overlay_name, String destroyed_overlay_name)
	{
		this.setPosition(row, col);
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
		list = new ItemList(1);
	}
	
	public MapSquare(MapSquare square)
	{
		this(square.row, square.col, square.image_name, 
				square.overlay_name, square.destroyed_overlay_name);
		this.list = square.getItems();
		this.destroyed = square.destroyed;
		this.destructible = square.destructible;
		this.passThrough = square.passThrough;
		this.passThroughWhenDestroyed = square.passThroughWhenDestroyed;
		this.isMapLink = square.isMapLink;
		this.linked_map = square.linked_map;
		this.link_row = square.link_row;
		this.link_col = square.link_col;
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
		
		Iterator<Item> it = list.iterator();
		while(it.hasNext())
			it.next().loadImages(assets);
	}

	@Override
	public void render(Graphics2D g, RenderPanel panel) 
	{
		if(image != null)
			drawImage(panel, g, image, locX, locY, width, height);

		if(overlay != null && !destroyed)
			drawImage(panel, g, overlay, locX, locY, width, height);

		if(destroyed && destroyed_overlay != null)
			drawImage(panel, g, destroyed_overlay, locX, locY, width, height);
		
		Iterator<Item> it = list.iterator();
		while(it.hasNext())
			it.next().render(g, panel);
	}

	@Override
	public String getRenderName() 
	{
		return "Map Square: " + image_name;
	}

	/**
	 * Set whether or not this square is destroyed.
	 * @param destroyed Whether or not this square is destroyed.
	 */
	public void setDestroyed(boolean destroyed)
	{
		if(destructible)
			this.destroyed = destroyed;
		else this.destroyed = false;
	}

	/**
	 * Returns whether or not this space is destroyed.
	 * @return Whether or not this space is destroyed.
	 */
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	/**
	 * Returns whether or not a map link lands here.
	 * @return Whether or not a map link lands here.
	 */
	public boolean isLinkLanding()
	{
		return isLinkLanding;
	}
	
	/**
	 * Returns whether or not this space can be passed through.
	 * @return Whether or not this space can be passed through.
	 */
	public boolean isPassable()
	{
		return passThrough;
	}
	
	/**
	 * Called when an item is dropped on this space. This could
	 * be an item or an item stack.
	 * @param item The item that was dropped.
	 */
	public boolean itemDropped(Item item)
	{
		boolean result = list.add(item);
		updateItemProperties();
		return result;
	}
	
	/**
	 * Update the positional properties of all of the items on this square.
	 */
	public void updateItemProperties()
	{
		Iterator<Item> it = list.iterator();
		while(it.hasNext())
		{
			Item i = it.next();
			i.setLocation(locX, locY);
			i.setSize(width);
		}
	}
	
	public int getRow() { return row; }
	public int getColumn() { return col; }
	public void setRow(int row) { this.row = row; }
	public void setColumn(int col) { this.col = col; }
	
	/**
	 * Set the position of this square in the map.
	 * @param row The row of this square.
	 * @param col The column of this square.
	 */
	public void setPosition(int row, int col)
	{
		this.row = row;
		this.col = col;
		positionUpdated();
	}
	
	/**
	 * Update the render properties based on the new row
	 * and column.
	 */
	private void positionUpdated()
	{
		this.locX = col;
		this.locY = row;
		this.width = 1;
		this.height = 1;
	}
	
	/**
	 * Get all of the items on the MapSquare.
	 * @return The list of items on the MapSquare.
	 */
	public ItemList getItems()
	{
		return list;
	}
	
	/**
	 * Returns whether or not this square contains an item with the given UUID.
	 * @param uuid The UUID of the item to search for.
	 * @return Whether or not the item is on this square.
	 */
	public boolean containsItem(ItemUUID uuid)
	{
		Iterator<Item> it = list.iterator();
		while(it.hasNext())
		{
			Item i = it.next();
			if(i.getUUID().equals(uuid))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Remove an item from this map square. Returns the item that was removed.
	 * @param uuid The UUID of the item to remove.
	 * @return The item if it was removed, null if the item was not removed.
	 */
	public Item removeItem(ItemUUID uuid)
	{
		Iterator<Item> it = list.iterator();
		while(it.hasNext())
		{
			Item i = it.next();
			if(i.getUUID().equals(uuid))
			{
				it.remove();
				return i;
			}
		}
		
		return null;
	}
	
	public String getImageName() { return image_name; }
	public String getOverlayName() { return overlay_name; }
	public String getDestroyedOverlayName() { return destroyed_overlay_name; }
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof MapSquare)
		{
			MapSquare square = (MapSquare)obj;
			if(square.getRow() != row)
				return false;
			if(square.getColumn() != col)
				return false;
			if(square.getImageName() != image_name)
				return false;
			if(square.getOverlayName() != overlay_name)
				return false;
			if(square.getDestroyedOverlayName() != destroyed_overlay_name)
				return false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Receive a game tick.
	 */
	public void tick()
	{
		ticks++;
		if(ticks > regenerate && regenerate >= 0)
		{
			ready = true;
			ticks = 0;
			destroyed = false;
		}
	}
	
	/**
	 * Make this square ready for generation (game is starting)
	 */
	public void reset()
	{
		ticks = 0;
		ready = true;
	}
	
	/**
	 * Returns whether or not this square is ready to generate a new item.
	 * @return Whether or not this square is ready to generate a new item.
	 */
	public boolean isReady()
	{
		return ready;
	}
	
	/**
	 * Generate the item that this space generates.
	 * @return The item that this square generates, null if this square
	 * isn't ready to generate a new item.
	 */
	public synchronized Item generate()
	{
		if(ready)
		{
			ticks = 0;
			destroyed = true;
			ready = false;
			return new Item(generate, true);
		}
		
		return null;
	}
	
	/**
	 * Returns whether or not this space generates items.
	 * @return Whether or not this space generates items.
	 */
	public boolean generates()
	{
		return generate != null;
	}
	
	protected int row; /**< The row of this map square. */
	protected int col; /**< The column of this map square. */
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
	protected ItemList list; /**< All of the items on the square. */
	
	/** Generate properties */
	protected Item generate; /**< The item to generate */
	protected int regenerate; /**< The regenerate rate */
	protected int ticks; /**< The amount of ticks that have passed */
	protected boolean ready; /**< Whether or not we are ready to generate a new item */
	
	private static final long serialVersionUID = -5877817070442524231L;
}
