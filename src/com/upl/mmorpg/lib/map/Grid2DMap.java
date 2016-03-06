package com.upl.mmorpg.lib.map;

import com.upl.mmorpg.lib.gui.RenderPanel;

public class Grid2DMap
{
	public Grid2DMap(RenderPanel panel)
	{
		this.panel = panel;
		loaded = false;
		map = null;
	}
	
	public void createNewMap(int rows, int cols)
	{
		map = new MapSquare[rows][cols];
		for(int r = 0;r < rows;r++)
			for(int c = 0;c < cols;c++)
				map[r][c] = null;
		
		this.rowCount = rows;
		this.colCount = cols;
		loaded = true;
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
	
	public void deleteSquare(int row, int col)
	{
		if(!loaded) return;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;
		if(map[row][col] != null)
			panel.removeBPRenderable(map[row][col]);
		map[row][col] = null;
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
	
	private int rowCount;
	private int colCount;
	
	private boolean loaded;
	private MapSquare[][] map;
	
	private RenderPanel panel;
}
