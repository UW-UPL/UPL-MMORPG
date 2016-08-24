package com.upl.mmorpg.game;

import com.upl.mmorpg.lib.gui.Renderable;

public abstract class GridRenderable extends Renderable
{
	public GridRenderable(int row, int col)
	{
		this.row = row;
		this.col = col;
		positionUpdated();
	}
	
	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }
	public int getColumn() { return col; }
	public void setColumn(int col) { this.col = col; }
	
	private void positionUpdated()
	{
		this.locX = col;
		this.locY = row;
	}
	
	private int row; /**< The row of the renderable */
	private int col; /**< The column of the renderable */
	
	private static final long serialVersionUID = -4852028202705458744L;
}
