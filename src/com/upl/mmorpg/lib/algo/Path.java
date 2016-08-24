package com.upl.mmorpg.lib.algo;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.gui.RenderMath;

public class Path implements Serializable
{
	public Path() 
	{
		path = new LinkedList<GridPoint>();
	}
	
	public void addPoint(int row, int col)
	{
		path.add(new GridPoint(row, col));
	}
	
	private void addPoint(GridPoint point)
	{
		path.add(new GridPoint(point.getRow(), point.getColumn()));
	}
	
	public void addFirstPoint(int row, int col)
	{
		path.addFirst(new GridPoint(row, col));
	}
	
	public GridPoint removeLastPoint()
	{
		return path.removeLast();
	}
	
	public void catPath(Path path)
	{
		System.out.println("ORIGINAL:");
		print();
		System.out.println("APPEND:");
		path.print();
		Path copy = path.copy();
		
		/* Does the last point need to be removed? */
		if(copy.getNextRow() == getLast().getRow()
				&& copy.getNextCol() == getLast().getColumn())
			copy.moveForward();
		
		Iterator<GridPoint> it = copy.iterator();
		while(it.hasNext())
			addPoint(it.next());
		System.out.println("RESULT:");
		print();
	}
	
	public Iterator<GridPoint> iterator()
	{
		return path.iterator();
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
			double y1 = currPoint.getColumn();
			double x2 = nextPoint.getRow();
			double y2 = nextPoint.getColumn();
			
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
		return path.getFirst().getColumn();
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
			point.setColumn(point.getColumn() + col);
		}
	}
	
	public void print()
	{
		int x = 0;
		Iterator<GridPoint> it = path.iterator();
		while(it.hasNext())
		{
			GridPoint point = it.next();
			System.out.println(x + ": " + point.getRow() + "," + point.getColumn());
			x++;
		}
	}
	
	public void optimize()
	{
		Iterator<GridPoint> it = path.iterator();
		GridPoint last = null;
		while(it.hasNext())
		{
			if(last == null)
				last = it.next();
			else {
				GridPoint curr = it.next();
				if(last.equals(curr))
					it.remove();
				else last = curr;
			}
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
	
	private static final long serialVersionUID = 9130629493103118867L;
}
