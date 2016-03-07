package com.upl.mmorpg.lib.map;

import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.libfile.FileManager;

public class Grid2DMap
{
	public Grid2DMap(RenderPanel panel)
	{
		this.panel = panel;
		loaded = false;
		map = null;
	}
	
	public void unload()
	{
		map = null;
		loaded = false;
	}
	
	public void addToPanel()
	{
		for(int r = 0;r < rowCount;r++)
		{
			for(int c = 0;c < colCount;c++)
			{	
				if(map[r][c] != null)
					panel.addBPRenderable(map[r][c]);
			}
		}
	}
	
	public void setSquare(int row, int col, MapSquare square)
	{
		if(!loaded) return;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;
		
		if(map[row][col] != null)
			panel.removeBPRenderable(map[row][col]);
		panel.addBPRenderable(square);
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
			
			System.out.println("ADDING " + row + " " + col);
			StringBuilder squareIn = new StringBuilder(line);
			String s = squareIn.substring(parts[0].length() + parts[1].length() + 2);
			System.out.println("setup: " + s);
			MapSquare square = MapSquare.import_square(s, assets, x, y, tile_size);
			square.loadImages();
			setSquare(row, col, square);
			map[row][col] = square;
			panel.addBPRenderable(square);
		}
		
		return true;
	}
	
	public MapSquare getSquare(int row, int col)
	{
		if(!loaded) return null;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return null;
		return map[row][col];
	}
	
	protected int rowCount;
	protected int colCount;
	
	protected boolean loaded;
	private MapSquare[][] map;
	
	protected RenderPanel panel;
}
