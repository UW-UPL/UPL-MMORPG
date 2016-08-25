package com.upl.mmorpg.game.item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;

/**
 * This is the base class for all items in the game.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class Item extends Renderable implements Serializable
{
	/**
	 * Copy an item exactly, except generate a new uuid if the boolean is true.
	 * @param i The item to make an exact copy of.
	 * @param newItem Whether or not this needs a new UUID.
	 */
	public Item(Item i, boolean newItem)
	{
		id = i.id;
		if(newItem)
			uuid = ItemUUID.generate();
		else uuid = i.uuid;
		type = i.type;
		name = i.name;
		asset_path = i.asset_path;
		value = i.value;
		asset = i.asset;
	}
	
	/**
	 * Copy an item exactly (includes exact UUID).
	 * @param i The item to make an exact copy of.
	 */
	public Item(Item i)
	{
		this(i, false);
	}
	
	/**
	 * Generate an item from the given properties.
	 * @param id The id of the item.
	 * @param type The type of item.
	 * @param name The name of the item.
	 * @param asset_path The path to the asset for this item.
	 * @param value The game value of the item.
	 */
	public Item(int id, Item.Type type, String name, String asset_path, long value)
	{
		this.id = id;
		this.uuid = ItemUUID.generate();
		this.type = type;
		this.name = name;
		this.asset_path = asset_path;
		this.value = value;
		this.asset = null;
	}
	
	/**
	 * For items that will be displayed. The properties are copied from the template object
	 * and the location data is copied as needed.
	 * @param i The item to base this item off of.
	 * @param row The row of the item.
	 * @param col The column of the item.
	 * @param tile_size The size of a map tile.
	 */
	public Item(Item i, int row, int col, double tile_size)
	{
		this(i, false);
		setLocation(col * tile_size, row * tile_size);
		setSize(tile_size);
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel) 
	{		
		if(asset == null)
			return;
		
		drawImage(panel, g, asset, locX, locY, width, height);
	}

	@Override
	public String getRenderName() 
	{
		return name;
	}
	
	@Override
	public void loadImages(AssetManager assets) throws IOException
	{
		asset = assets.loadImage(asset_path);
	}
	
	/**
	 * Set the position of this item on the map. 
	 * @param row The row of the item.
	 * @param col The column of the item.
	 */
	public void setPosition(int row, int col)
	{
		this.locX = col;
		this.locY = row;
		this.width = 1;
		this.height = 1;
	}
	
	public int getId() { return id; }
	public Item.Type getType() { return type; }
	public String getName() { return name; }
	public long getValue() { return value; }
	public String getAssetPath() { return asset_path; }
	public ItemUUID getUUID() { return uuid; }

	private final int id; /**< The ID of the item */
	private final ItemUUID uuid; /**< The UUID for this item */
	private final Item.Type type; /**< The type of the object */
	private final String name; /**< The name of the item. */
	private final String asset_path; /**< The path of the image asset */
	private final long value; /**< The value of the item. */
	private transient BufferedImage asset; /**< The asset to draw for the item */
	
	public enum Type
	{
		NONE, GENERAL, QUEST
	}
	
	private static final long serialVersionUID = -887291186700078765L;
}
