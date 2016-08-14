package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

import com.upl.mmorpg.game.character.MMOCharacter;
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
	
	@Override
	public void render(Graphics2D g, RenderPanel panel, double zoom) 
	{
		if(!loaded || map == null) return;
		
		/* Only draw the tiles that are on the screen */
		double startX = panel.getGlobalViewX() - zoom;
		double startY = panel.getGlobalViewY() - zoom;
		
		int displayCols = (int)(panel.getWidth() / zoom) + 2;
		int displayRows = (int)(panel.getHeight() / zoom) + 3;
		
		int startRow = (int)(startY / zoom);
		int startCol = (int)(startX / zoom);
		
		for(int rows = 0;rows < displayRows;rows++)
		{
			for(int cols = 0;cols < displayCols;cols++)
			{
				int row = rows + startRow;
				int col = cols + startCol;
				
				if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
					continue;
				if(map[row][col] != null)
					map[row][col].render(g, panel, zoom);
			}
		}
	}

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
		if(!loaded) return;
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
			Grid2DMap grid = (Grid2DMap)buff.popObject();
			if(grid == null)
				return false;
			map = grid.map;
			rowCount = grid.rowCount;
			colCount = grid.colCount;
			loaded = true;
			generateSquareProperties();
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
		if(!loaded) return null;
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
	
	protected transient int id; /**< The id number for this map (set by Game). */
	protected transient boolean loaded; /**< Whether or not the assets have been loaded for this map. */
	protected transient LinkedList<MMOCharacter> characters; /**< The characters that are on this map */
	
	protected int rowCount; /**< How many rows are in this map */
	protected int colCount; /**< How many columns are in this map. s*/

	protected MapSquare[][] map; /**< The 2D representation of the game map.  */
	
	private static final long serialVersionUID = -6200242944785221212L;
}
