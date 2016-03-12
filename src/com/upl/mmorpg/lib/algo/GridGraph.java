package com.upl.mmorpg.lib.algo;

import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.gui.RenderMath;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.map.MapSquare;

public class GridGraph 
{
	public GridGraph(int start_row, int start_col, Grid2DMap map)
	{
		this.original_start_row = start_row;
		this.original_start_col = start_col;
		this.max_dist = MAX_PATH;
		this.start_row = start_row - max_dist;
		this.start_col = start_col - max_dist;
		
		this.max_row = (max_dist * 2) + 1;
		this.max_col = (max_dist * 2) + 1;
		
		graph = new GridNode[max_row][max_col];
		for(int r = 0;r < max_row;r++)
		{
			for(int c = 0;c < max_col;c++)
			{
				MapSquare square = map.getSquare(r + this.start_row, 
						c + this.start_col);
				
				if(square == null)
				{
					graph[r][c] = null;
					continue;
				}
				
				graph[r][c] = new GridNode(Integer.MAX_VALUE, 
						r, c, max_row, max_col,
						square.isPassable());
			}
		}
	}
	
	public Path shortestPathTo(int endRow, int endCol)
	{
		endRow -= start_row;
		endCol -= start_col;
		
		/* Dijkstra's shortest path algorithm */
		
		GridNode centerNode = graph[max_dist][max_dist];
		
		if(centerNode == null) return null;
		
		/* Set our node to 0 */
		centerNode.value = 0;
		centerNode.visited = true;
		
		double curr_dist = 0.0d;
		PriorityQueue<Path> paths = new PriorityQueue<Path>(true);
		GridNode currentNode = centerNode;
		Path currPath = new Path();
		currPath.addPoint(centerNode.row, centerNode.col);
		paths.enqueue(0, currPath);
		
		while(true)
		{
			/* Get the current shortest path and current node */
			paths.sort();
			if(paths.empty())
				break;
			currPath = paths.dequeueLowestPriority();
			currentNode = graph[currPath.getLast().getRow()]
					[currPath.getLast().getCol()];
			curr_dist = currentNode.value;
			
			double x1 = currentNode.col;
			double y1 = currentNode.row;
			
			/* Get neighbors */
			LinkedList<GridNode> neighbors = currentNode.getNeighbors();
			
			Iterator<GridNode> it = neighbors.iterator();
			while(it.hasNext())
			{
				GridNode node = it.next();
				if(node.visited || !node.passable) continue;
				
				double x2 = node.col;
				double y2 = node.row;
				double dist = RenderMath.pointDistance(x1, y1, x2, y2);
				double newdist = curr_dist + dist;
				
				/* Set the value for this node */
				node.value = newdist;
				node.visited = true;
				
				Path newPath = currPath.copy();
				newPath.addPoint(node.row, node.col);
				if(node.row == endRow && node.col == endCol)
				{
					newPath.translate(-max_dist + original_start_row, 
							-max_dist + original_start_col);
					return newPath;
				}
				
				/* Add this path to the queue*/
				paths.enqueue(node.value, newPath);
			}
		}
		
		return null;
	}
	
	private int start_row;
	private int start_col;
	private int max_dist;
	private int max_row;
	private int max_col;
	private int original_start_row;
	private int original_start_col;
	
	public GridNode graph[][];
	
	private static final int MAX_PATH = 150;
	
	private class GridNode
	{
		public GridNode(double value, int row, int col, 
				int max_row, int max_col, boolean passable)
		{
			this.value = value;
			this.row = row;
			this.col = col;
			this.max_row = max_row;
			this.max_col = max_col;
			this.passable = passable;
			this.visited = false;
		}
		
		public LinkedList<GridNode> getNeighbors()
		{
			LinkedList<GridNode> nodes = new LinkedList<GridNode>();
			GridNode right = getRight();
			GridNode left = getLeft();
			GridNode up = getUp();
			GridNode down = getDown();
			
			if(right != null)  nodes.add(getRight());
			if(left != null)  nodes.add(getLeft());
			if(up != null)  nodes.add(getUp());
			if(down != null)  nodes.add(getDown());
			
			if((up == null || up.passable) && (left == null || left.passable))
				if(getUpLeft() != null)  nodes.add(getUpLeft());
			if((up == null || up.passable) && (right == null || right.passable))
				if(getUpRight() != null)  nodes.add(getUpRight());
			if((down == null || down.passable) && (left == null || left.passable))
				if(getDownLeft() != null)  nodes.add(getDownLeft());
			if((down == null || down.passable) && (right == null || right.passable))
				if(getDownRight() != null)  nodes.add(getDownRight());
			
			return nodes;
		}
		
		public GridNode getRight() 
		{
			int r = row;
			int c = col + 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getLeft() 
		{
			int r = row;
			int c = col - 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getUp() 
		{
			int r = row - 1;
			int c = col;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getDown() 
		{
			int r = row + 1;
			int c = col;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getUpRight() 
		{
			int r = row - 1;
			int c = col + 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getUpLeft() 
		{
			int r = row - 1;
			int c = col - 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getDownRight() 
		{
			int r = row + 1;
			int c = col + 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		public GridNode getDownLeft() 
		{
			int r = row + 1;
			int c = col - 1;
			
			if(r < 0 || r >= max_row || c < 0 || c >= max_col)
				return null;
			
			return graph[r][c];
		}
		
		private double value;
		private int row;
		private int col;
		private int max_row;
		private int max_col;
		private boolean passable;
		private boolean visited;
	}
}
