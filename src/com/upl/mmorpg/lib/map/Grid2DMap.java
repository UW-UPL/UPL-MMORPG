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
	 * Load a Grid2DMap from a file.
	 * @param file_name The name of the file that contains the Grid2D map.
	 * @param assets The asset manager that should be used as a cache.
	 * @param tile_size The display size of a game tile.
	 * @return A reference to a new Grid2DMap on success, null on failure.
	 * @throws IOException The IOException that occurred opening the file.
	 */
	public static Grid2DMap load(String file_name, AssetManager assets, double tile_size) throws IOException
	{
		FileManager file = assets.getFile(file_name, false, true, false);
		StackBuffer buff = new StackBuffer(file);
		file.close();
		
		Object load = buff.popObject();
		if(load instanceof Grid2DMap)
			return (Grid2DMap)load;
		else return null;
	}
	
	/**
	 * Load all of the assets used by the game tiles.
	 * @throws IOException The IOException that occurred openeing image files.
	 */
	public void loadAllImages() throws IOException
	{
		for(int row = 0;row < rowCount;row++)
		{
			for(int col = 0;col < colCount;col++)
			{
				if(map[row][col] != null)
					map[row][col].loadImages();
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
	
	protected double tileSize; /**< Display size of a map square. */
	
	protected int rowCount; /**< How many rows are in this map */
	protected int colCount; /**< How many columns are in this map. s*/
	
	protected boolean loaded; /**< Whether or not the assets have been loaded for this map. */
	protected boolean renderable; /**< Whether or not this map is renderable (server/client) */
	protected MapSquare[][] map; /**< The 2D representation of the game map.  */
	
	protected transient RenderPanel panel; /**< The panel that should be used for rendering game squares (client) */
	
	private static final long serialVersionUID = -6200242944785221212L;
}
