package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.libfile.FileManager;

public class Grid2DMap extends Renderable
{
	public Grid2DMap()
	{
		map = null;
		loaded = false;
		renderable = false;
		tileSize = 0.0d;
	}
	
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
		if(!renderable) return;
		
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
				
				map[row][col].render(g);
			}
		}
	}

	@Override
	public String getRenderName() 
	{
		return "Grid2DMap";
	}
	
	public void unload()
	{
		map = null;
		loaded = false;
	}
	
	public void setSquare(int row, int col, MapSquare square)
	{
		if(!loaded) return;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;
		map[row][col] = square;
	}
	
	public boolean load(String file_name, AssetManager assets, double tile_size) throws IOException
	{
		if(loaded) return false;
		
		FileManager file = assets.getFile(file_name, true, false);
		
		/* Get the rows and cols */
		String line1[] = file.readLine().split(",");
		this.rowCount = Integer.parseInt(line1[0].trim());
		this.colCount = Integer.parseInt(line1[1].trim());
		
		if(rowCount <= 0 || rowCount > 10000000
				|| colCount <= 0 || colCount > 10000000)
			return false;
		
		/* Create the map */
		map = new MapSquare[rowCount][colCount];
		for(int r = 0;r < rowCount;r++)
			for(int c = 0;c < colCount;c++)
				map[r][c] = null;
		
		String line = null;
		while((line = file.readLine()) != null)
		{
			if(line.trim().equalsIgnoreCase(""))
				continue;
			
			String parts[] = line.split(",");
			int row = Integer.parseInt(parts[0]);
			int col = Integer.parseInt(parts[1]);
			
			double x = col * tile_size;
			double y = row * tile_size;
			
			StringBuilder squareIn = new StringBuilder(line);
			String s = squareIn.substring(parts[0].length() + parts[1].length() + 2);
			MapSquare square = MapSquare.import_square(s, assets, x, y, tile_size);
			setSquare(row, col, square);
			map[row][col] = square;
		}
		
		loaded = true;
		
		return true;
	}
	
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
	
	public MapSquare getSquare(int row, int col)
	{
		if(!loaded) return null;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return null;
		return map[row][col];
	}
	
	protected double tileSize;
	
	protected int rowCount;
	protected int colCount;
	
	protected boolean loaded;
	protected boolean renderable;
	protected MapSquare[][] map;
	
	protected RenderPanel panel;
}
