package com.upl.mmorpg.lib.algo;

import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.gui.RenderMath;

public class Path
{
	public Path() 
	{
		path = new LinkedList<GridPoint>();
	}
	
	public void addPoint(int row, int col)
	{
		path.add(new GridPoint(row, col));
	}
	
	public GridPoint getLast()
	{
		return path.getLast();
	}
	
	public boolean isEmpty()
	{
		return path.isEmpty();
	}
	
	public double calculatePathLength()
	{
		if(path.size() <= 1) return 0;
		double length = 0.0;
		
		Iterator<GridPoint> it = path.iterator();
		GridPoint currPoint = it.next();
		while(it.hasNext())
		{
			GridPoint nextPoint = it.next();
			
			double x1 = currPoint.getRow();
			double y1 = currPoint.getCol();
			double x2 = nextPoint.getRow();
			double y2 = nextPoint.getCol();
			
			length += RenderMath.pointDistance(x1, y1, x2, y2);
			currPoint = nextPoint;
		}
		
		return length;
	}
	
	public int getNextRow()
	{
		return path.getFirst().getRow();
	}
	
	public int getNextCol()
	{
		return path.getFirst().getCol();
	}
	
	public Path copy()
	{
		Path path = new Path();
		path.path.addAll(this.path);
		
		return path;
	}
	
	public void translate(int row, int col)
	{
		Iterator<GridPoint> it = path.iterator();
		while(it.hasNext())
		{
			GridPoint point = it.next();
			point.setRow(point.getRow() + row);
			point.setCol(point.getCol() + col);
		}
	}
	
	public void print()
	{
		int x = 0;
		Iterator<GridPoint> it = path.iterator();
		while(it.hasNext())
		{
			GridPoint point = it.next();
			System.out.println(x + ": " + point.getRow() + "," + point.getCol());
			x++;
		}
	}
	
	public int size()
	{
		return path.size();
	}
	
	public void moveForward()
	{
		path.removeFirst();
	}
	
	private LinkedList<GridPoint> path;
}
