package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Serializable;

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
	public Grid2DMap(double tileSize)
	{
		map = null;
		loaded = false;
		renderable = false;
		this.tileSize = tileSize;
	}
	
	/**
	 * Create a new Grid2DMap that can be used for rendering.
	 * @param panel The panel to render the map on.
	 * @param tileSize The default tile size based on screen resolution.
	 */
	public Grid2DMap(RenderPanel panel, double tileSize)
	{
		this.panel = panel;
		this.tileSize = tileSize;
		loaded = false;
		renderable = true;
		map = null;
	}
	
	/**
	 * Initialize a map from the contents of a map file.
	 * @param file_name The map file to initialize the Grid2DMap from.
	 * @param assets The current asset manager.
	 * @param tile_size The size of a single tile.
	 * @throws IOException If the file doesn't exist.
	 */
	public Grid2DMap(String file_name, AssetManager assets, double tile_size) throws IOException
	{
		this(tile_size);
		if(!load(file_name, assets, tile_size))
			throw new IOException("Ilegal map format exception");
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		if(!renderable || !loaded || map == null) return;
		
		/* Only draw the tiles that are on the screen */
		double startX = panel.getViewX() - tileSize;
		double startY = panel.getViewY() - tileSize;
		
		int displayCols = (int)(panel.getWidth() / tileSize) + 2;
		int displayRows = (int)(panel.getHeight() / tileSize) + 3;
		
		int startRow = (int)(startY / tileSize);
		int startCol = (int)(startX / tileSize);
		
		for(int rows = 0;rows < displayRows;rows++)
		{
			for(int cols = 0;cols < displayCols;cols++)
			{
				int row = rows + startRow;
				int col = cols + startCol;
				
				if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
					continue;
				if(map[row][col] != null)
					map[row][col].render(g);
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
	 * Returns the size of a tile on this map.
	 * @return The size of a tile.
	 */
	public double getTileSize()
	{
		return this.tileSize;
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
	public boolean load(String file_name, AssetManager assets, double tile_size) throws IOException
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
		
		renderable = true;
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
	
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 93b067a241dd3045f55f410e446950fbd5110b9e
	public double getWidth() {
		return tileSize * colCount;
	}
	
	public double getHeight() {
		return tileSize * rowCount;
	}
	
<<<<<<< HEAD
	protected double tileSize;
=======
=======
>>>>>>> 93b067a241dd3045f55f410e446950fbd5110b9e
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
					map[row][col].setX(col * tileSize);
					map[row][col].setY(row * tileSize);
					map[row][col].setWidth(tileSize);
					map[row][col].setHeight(tileSize);
					map[row][col].setRotation(0.0f);
					map[row][col].updateItemProperties();
				}
	}
	
	protected transient double tileSize; /**< Display size of a map square. */
	protected transient boolean loaded; /**< Whether or not the assets have been loaded for this map. */
	protected transient boolean renderable; /**< Whether or not this map is renderable (server/client) */
	protected transient RenderPanel panel; /**< The panel that should be used for rendering game squares (client) */
<<<<<<< HEAD
>>>>>>> 0534434b097357a2e4ff215d22225008c9e17e5a
=======
>>>>>>> 93b067a241dd3045f55f410e446950fbd5110b9e
	
	protected int rowCount; /**< How many rows are in this map */
	protected int colCount; /**< How many columns are in this map. s*/
	
	protected MapSquare[][] map; /**< The 2D representation of the game map.  */
	
	private static final long serialVersionUID = -6200242944785221212L;
}
