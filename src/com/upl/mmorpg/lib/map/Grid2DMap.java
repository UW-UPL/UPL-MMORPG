package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class Grid2DMap extends Renderable implements Serializable
{
	/**
	 * Create a Grid2DMap that can be used for a headless server (no graphic rendering).
	 * @param tileSize The tile size that should be used.
	 */
	public Grid2DMap(int map_id)
	{
		map = null;
		loaded = false;
		this.id = map_id;
		characters = new LinkedList<MMOCharacter>();
		items = new LinkedList<Item>();
	}
	
	/**
	 * Initialize a map from the contents of a map file.
	 * @param file_name The map file to initialize the Grid2DMap from.
	 * @param assets The current asset manager.
	 * @param tile_size The size of a single tile.
	 * @throws IOException If the file doesn't exist.
	 */
	public Grid2DMap(String file_name, AssetManager assets, int map_id) throws IOException
	{
		if(!load(file_name, assets))
			throw new IOException("Ilegal map format exception");
	}
	
	/**
	 * Make a copy of a Grid2DMap.
	 * @param map The map to copy.
	 */
	public Grid2DMap(Grid2DMap map)
	{
		this.map = new MapSquare[map.getRows()][map.getColumns()];
		this.rowCount = map.getRows();
		this.colCount = map.getColumns();
		this.id = map.getID();
		
		for(int row = 0;row < map.getRows();row++)
			for(int col = 0;col < map.getColumns();col++)
			{
				MapSquare square = map.getSquare(row, col);
				if(square != null)
					this.map[row][col] = new MapSquare(square);
			}
		
		this.loaded = true;
		characters = new LinkedList<MMOCharacter>();
		items = new LinkedList<Item>();
		findAllItems();
		generateSquareProperties();
	}
	
	/**
	 * Let this map know that it has been loaded. This is only
	 * used when the map is tranferred over the network and the
	 * loaded state is not preserved.
	 */
	public void setLoaded()
	{
		this.loaded = true;
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel) 
	{
		if(!loaded || map == null) return;
		
		double zoom = panel.getZoom();
		
		int startRow = (int)(panel.getViewY() / zoom);
		int startCol = (int)(panel.getViewX() / zoom);
		
		int displayCols = (int)(panel.getWidth() / zoom) + 2;
		int displayRows = (int)(panel.getHeight() / zoom) + 2;
		
		for(int rows = 0;rows < displayRows;rows++)
		{
			for(int cols = 0;cols < displayCols;cols++)
			{
				int row = rows + startRow;
				int col = cols + startCol;
				
				if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
					continue;
				if(map[row][col] != null)
					map[row][col].render(g, panel);
			}
		}
	}
	
	public int getRows() { return rowCount; }
	public int getColumns() { return colCount; }

	@Override
	public String getRenderName() 
	{
		return "Grid2DMap";
	}
	
	/**
	 * Release the assets for this grids
	 */
	public void unload()
	{
		map = null;
		loaded = false;
	}
	
	/**
	 * Get the ID of the map.
	 * @return The ID of the map.
	 */
	public int getID()
	{
		return id;
	}
	
	/**
	 * Set the current ID of the map.
	 * @param map_id The new map ID.
	 */
	public void setID(int map_id)
	{
		this.id = map_id;
	}
	
	/**
	 * Set a map square on the map.
	 * @param row The row of the map square.
	 * @param col The column of the map square.
	 * @param square The square to put on the map.
	 */
	public void setSquare(int row, int col, MapSquare square)
	{
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;
		map[row][col] = square;
	}
	

	/**
	 * Load a map from a file and overwrite the current map.
	 * @param file_name The filename of the map
	 * @param assets The current asset manager.
	 * @param tile_size The size of a single tile.
	 * @return Whether or not the map could be loaded.
	 * @throws IOException If the file couldn't be found.
	 */
	public boolean load(String file_name, AssetManager assets) throws IOException
	{
		FileManager file = assets.getFile(file_name, false, true, false);
		if(!file.opened())
			return false;
		StackBuffer buff = new StackBuffer(file);
		file.close();
		
		Object load = buff.popObject();
		
		if(load instanceof Grid2DMap)
		{
			Grid2DMap grid = (Grid2DMap)load;
			map = grid.map;
			rowCount = grid.rowCount;
			colCount = grid.colCount;
			loaded = true;
			characters = new LinkedList<MMOCharacter>();
			generateSquareProperties();
			findAllItems();
		} else return false;
		
		return true;
	}
	
	/**
	 * Load all of the assets used by the game tiles.
	 * @throws IOException The IOException that occurred openeing image files.
	 */
	public void loadAllImages(AssetManager assets) throws IOException
	{
		for(int row = 0;row < rowCount;row++)
		{
			for(int col = 0;col < colCount;col++)
			{
				if(map[row][col] != null)
					map[row][col].loadImages(assets);
			}
		}
	}
	
	/**
	 * Returns a map square.
	 * @param row The row of the square.
	 * @param col The column of the square.
	 * @return The map square at the given row and column.
	 */
	public MapSquare getSquare(int row, int col)
	{
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return null;
		return map[row][col];
	}
	
	/**
	 * Regenerates the properties of all of the map squares. This should be
	 * called after the tile size is changed.
	 */
	public void generateSquareProperties()
	{
		for(int row = 0;row < rowCount;row++)
			for(int col = 0;col < colCount;col++)
				if(map[row][col] != null)
				{
					map[row][col].setX(col);
					map[row][col].setY(row);
					map[row][col].setWidth(1);
					map[row][col].setHeight(1);
					map[row][col].setRotation(0.0f);
					map[row][col].updateItemProperties();
				}
	}
	
	/**
	 * Add a character to a map.
	 * @param character The character to add to the map.
	 */
	public void addCharacter(MMOCharacter character)
	{
		characters.add(character);
	}
	
	/**
	 * Remove a player from a map.
	 * @param character The character to remove from the map.
	 * @return Whether or not the character could be removed from the map.
	 */
	public boolean removeCharacter(MMOCharacter character)
	{
		return characters.remove(character);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Grid2DMap)
		{
			Grid2DMap map = (Grid2DMap)o;
			if(!(map.getRows() == this.rowCount
					&& map.getColumns() == this.colCount))
				return false;
			
			for(int row = 0;row < this.rowCount;row++)
				for(int col = 0;col < this.colCount;col++)
				{
					MapSquare square = map.getSquare(row, col);
					if((this.map[row][col] == null)
							!= (square == null))
						return false;
					if(square != null)
						if(!square.equals(this.map[row][col]))
							return false;
				}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the list of all characters on the map.
	 * @return The list of characters on the map.
	 */
	public LinkedList<MMOCharacter> getCharacters()
	{
		return characters;
	}
	
	/**
	 * Remove all characters from the map.
	 */
	public void clearCharacters()
	{
		characters.clear();
	}
	
	/**
	 * Add all of the given characters to this map.
	 * @param c The Collection of characters to add to this map.
	 */
	public void addAllCharacters(Collection<MMOCharacter> c)
	{
		characters.addAll(c);
	}
	
	/**
	 * Set the list of characters on the map.
	 * @param characters The characters that should be on the map.
	 */
	public void setCharacters(LinkedList<MMOCharacter> characters)
	{
		this.characters = characters;
	}
	
	/**
	 * Populate the internal item list with all of the items
	 * on the map.
	 * @return Whether or not items were found.
	 */
	public boolean findAllItems()
	{
		if(items == null)
			items = new LinkedList<Item>();
		items.clear();
		boolean added = false;
		for(int row = 0;row < rowCount;row++)
		{
			for(int col = 0;col < colCount;col++)
			{
				items.addAll(map[row][col].getItems());
				added = true;
			}
		}
		
		return added;
	}
	
	/**
	 * Returns the list of items on the map.
	 * @return The list of items on the map.
	 */
	public LinkedList<Item> getItems()
	{
		return items;
	}
	
	/**
	 * Convert the item from a uuid into the item object.
	 * @param uuid The uuid of the item to search for.
	 * @return The item, if it exists, null if it doesn't
	 */
	public Item getItem(ItemUUID uuid)
	{
		Iterator<Item> it = items.iterator();
		while(it.hasNext())
		{
			Item i = it.next();
			if(i.getUUID().equals(uuid))
				return i;
		}
		
		return null;
	}
	
	/**
	 * Drop an item onto the map.
	 * @param item The item to drop.
	 * @param row The row in which to drop the item.
	 * @param col The column in which to drop the item.
	 * @return Whether or not the item could be added to the map square.
	 */
	public boolean itemDropped(int row, int col, Item item)
	{
		if(row < rowCount && row >= 0
				&& col >= 0 && col < colCount)
		{
			MapSquare square = map[row][col];
			if(square != null)
			{
				if(square.itemDropped(item))
				{
					items.add(item);
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Pickup an item from the map.
	 * @param item The UUID of the item to pick up.
	 * @return The item, null if there is no such item on the map.
	 */
	public Item pickupItem(ItemUUID item)
	{
		for(int row = 0;row < rowCount;row++)
		{
			for(int col = 0;col < colCount;col++)
			{
				Item i = map[row][col].removeItem(item);
				if(i != null)
				{
					items.remove(i);
					return i;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Update this map with the properties of the new map.
	 * @param map The new map to get properties from.
	 */
	public boolean update(Grid2DMap map)
	{
		/* Assume both maps must have the same dimensions */
		if(map.rowCount != rowCount || map.colCount != colCount)
			return false;
		
		for(int row = 0;row < rowCount;row++)
			for(int col = 0;col < colCount;col++)
				this.map[row][col] = map.map[row][col];
		
		loaded = true;
		findAllItems();
		generateSquareProperties();
		return true;
	}
	
	protected transient int id; /**< The id number for this map (set by Game). */
	protected transient boolean loaded; /**< Whether or not the assets have been loaded for this map. */
	protected transient LinkedList<MMOCharacter> characters; /**< The characters that are on this map */
	protected transient LinkedList<Item> items; /**< The items on the map */
	
	protected int rowCount; /**< How many rows are in this map */
	protected int colCount; /**< How many columns are in this map. s*/

	protected MapSquare[][] map; /**< The 2D representation of the game map.  */
	
	private static final long serialVersionUID = -6200242944785221212L;
}
