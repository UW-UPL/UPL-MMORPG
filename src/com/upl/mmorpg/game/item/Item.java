package com.upl.mmorpg.game.item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class Item extends Renderable implements Serializable
{
	public Item(Item i)
	{
		id = i.id;
		type = i.type;
		name = i.name;
		asset_path = i.asset_path;
		value = i.value;
		asset = i.asset;
	}
	
	public Item(int id, Item.Type type, String name, String asset_path, long value)
	{
		this.id = id;
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
		this(i);
		setLocation(col * tile_size, row * tile_size);
		setSize(tile_size);
	}
	
	@Override
	public void render(Graphics2D g) 
	{		
		if(asset == null)
			return;
		
		g.drawImage(asset, (int)locX, (int)locY, (int)width, (int)height, null);
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
	
	public int getId() {return id;}
	public Item.Type getType() {return type;}
	public String getName() {return name;}
	public long getValue() {return value;}
	public String getAssetPath() {return asset_path;}

	private final int id; /**< The ID of the item */
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
