package com.upl.mmorpg.lib.algo;

public final class GridPoint 
{
	public GridPoint(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof GridPoint)
		{
			GridPoint p = (GridPoint)obj;
			if(p.row == this.row && p.col == this.col)
				return true;
		}
		
		return false;
	}
	
	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }
	public int getCol() { return col; }
	public void setCol(int col) {this.col = col;}
	
	private int row;
	private int col;
}
