package com.upl.mmorpg.lib.algo;

public final class GridPoint 
{
	public GridPoint(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }
	public int getCol() { return col; }
	public void setCol(int col) {this.col = col;}
	
	private int row;
	private int col;
}
