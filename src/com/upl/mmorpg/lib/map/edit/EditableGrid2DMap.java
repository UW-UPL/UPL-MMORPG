package com.upl.mmorpg.lib.map.edit;

import java.io.IOException;
import java.util.ArrayList;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class EditableGrid2DMap extends Grid2DMap
{
	public EditableGrid2DMap(RenderPanel panel, double tileSize) 
	{
		super(panel, tileSize);
	}

	public EditableGrid2DMap(double tile_size)
	{
		super(tile_size);
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
		FileManager file = assets.getFile(file_path, true, true);
		/* First export the map size */
		file.println(rowCount + "," + colCount);

		/* Export all of the squares */
		for(int row = 0;row < rowCount;row++)
		{
			for(int col = 0;col < colCount;col++)
			{
				if(map[row][col] == null) continue;
				StringBuilder out = new StringBuilder();
				out.append(row + "," + col + ",");
				out.append(map[row][col].export_square());
				file.println(out.toString());
			}
		}

		file.close();
	}

	@Override
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
		map = new EditableMapSquare[rowCount][colCount];
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
			EditableMapSquare square = EditableMapSquare.import_square(s, assets, x, y, tile_size);
			square.loadImages();
			setSquare(row, col, square);
			map[row][col] = square;
		}
		
		loaded = true;

		return true;
	}
	
	public void unload()
	{
		loaded = false;
		map = null;
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
		EditableGrid2DMap map = new EditableGrid2DMap(0.0d);
		map.load(file, assets, 1);
		
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
}
