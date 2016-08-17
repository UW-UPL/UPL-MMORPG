package com.upl.mmorpg.lib.map.edit;

import java.io.IOException;
import java.util.ArrayList;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.util.StackBuffer;

public class EditableGrid2DMap extends Grid2DMap
{
	public EditableGrid2DMap(int map_id)
	{
		super(map_id);
	}
	
	public EditableGrid2DMap(String file_name, AssetManager assets) throws IOException
	{
		super(0);
		
		if(!load(file_name, assets))
			throw new IOException("Ilegal map format exception");
	}
	
	public void createNewMap(int rows, int cols)
	{
		map = new EditableMapSquare[rows][cols];
		for(int r = 0;r < rows;r++)
			for(int c = 0;c < cols;c++)
				map[r][c] = null;

		this.rowCount = rows;
		this.colCount = cols;
		loaded = true;
	}

	public void deleteSquare(int row, int col)
	{
		if(!loaded) return;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;
		map[row][col] = null;
	}

	public void setSquare(int row, int col, EditableMapSquare square)
	{
		if(!loaded) return;
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return;

		map[row][col] = square;
	}

	public void export(String file_path, AssetManager assets) throws IOException
	{
		/* Convert this to a normal Grid2DMap */
		Grid2DMap map = new Grid2DMap(-1);
		for(int row = 0;row < rowCount;row++)
			for(int col = 0;col < colCount;col++)
				map.setSquare(row, col, ((EditableMapSquare)this.map[row][col]).export());
		
		FileManager file = assets.getFile(file_path, true, true, true);
		StackBuffer buff = new StackBuffer();
		buff.pushObject(map);
		byte[] arr = buff.toArray();
		file.writeBytes(arr);
		assets.closeFile(file);
	}
	
	public boolean load(String file_name, AssetManager assets, double tile_size) throws IOException
	{
		FileManager file = assets.getFile(file_name, false, true, false);
		if(!file.opened())
			return false;
		StackBuffer buff = new StackBuffer(file);
		file.close();
		
		Object obj = buff.popObject();
		
		if(obj instanceof EditableGrid2DMap)
		{
			EditableGrid2DMap grid = (EditableGrid2DMap)obj;
			
			this.map = grid.map;
			this.rowCount = grid.rowCount;
			this.colCount = grid.colCount;
			this.loaded = true;
		} else return false;
		
		return true;
	}
	
	public void unload()
	{
		loaded = false;
		map = null;
		rowCount = 0;
		colCount = 0;
	}

	public EditableMapSquare getSquare(int row, int col)
	{
		if(row < 0 || row >= rowCount || col < 0 || col >= colCount)
			return null;
		return (EditableMapSquare)map[row][col];
	}

	public static int[][] getAllLandings(String file, AssetManager assets) 
			throws IOException
	{
		EditableGrid2DMap map = new EditableGrid2DMap(file, assets);
		
		ArrayList<Integer> rows = new ArrayList<Integer>();
		ArrayList<Integer> cols = new ArrayList<Integer>();
		
		for(int row = 0;row < map.rowCount;row++)
		{
			for(int col = 0;col < map.colCount;col++)
			{
				if(map.getSquare(row, col) != null)
				{
					if(map.getSquare(row, col).isLinkLanding())
					{
						rows.add(row);
						cols.add(col);
					}
				}
			}
		}
		
		int result[][] = new int[rows.size()][2];
		for(int x = 0;x < rows.size();x++)
		{
			result[x][0] = rows.get(x);
			result[x][1] = cols.get(x);
		}
		
		return result;
	}
	
	private static final long serialVersionUID = -2224304768005340283L;
}
