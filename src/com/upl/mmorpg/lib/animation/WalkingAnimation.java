package com.upl.mmorpg.lib.animation;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.FollowListener;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(Game game, AnimationManager animation, 
			MMOCharacter character, AnimationListener listener)
	{
		super(game, animation, character, listener);
		this.vector_x = 0;
		this.vector_y = 0;
		arrived = false;
		interrupted =  false;
	}
	
	public void setPath(Path path)
	{
		if(path == null) return;
		this.walkingPath = path;
		interrupted = false;
		
		/* If we're at the first point, remove it */
		int myRow = character.getRow();
		int myCol = character.getCol();
		
		while(this.walkingPath.getNextCol() == myCol 
				&& this.walkingPath.getNextRow() == myRow)
			this.walkingPath.moveForward();
	}
	
	public Path getPath()
	{
		if(walkingPath != null) 
			return walkingPath.copy();
		else return null;
	}
	
	@Override
	public void animationInterrupted(Animation source) 
	{
		vector_x = 0;
		vector_y = 0;
	}
	
	@Override
	public void animationStarted() 
	{
		if(walkingPath == null) return;
		arrived = false;
		interrupted = false;
		if(!manager.setReel("walk_start", false))
			throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(100);
		
		int dir = this.lookTowards(walkingPath.getNextRow(), walkingPath.getNextCol());
		manager.setReelDirection(dir);
		this.generateVectors(dir);
		
		Iterator<FollowListener> it = character.getFollowers();
		while(it.hasNext())
		{
			it.next().characterMoving(character, 
					character.getRow(), 
					character.getCol());
		}
	}
	
	@Override
	public void animationReelFinished() 
	{
		interrupted = false;
		if(!arrived)
		{
			/* We need to start the walk animation*/
			if(!manager.setReel("walk", true))
				throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
			manager.setAnimationSpeed(15);
		}
	}
	
	public void interruptPath()
	{
		interrupted = true;
	}
	
	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}
	
	public void arrive()
	{
		manager.setReel("walk_end", false);
		manager.setAnimationSpeed(30);
	}
	
	public void appendWaypoint(int row, int col, Grid2DMap map)
	{
		GridGraph graph;
		
		if(walkingPath != null)
			graph = new GridGraph(walkingPath.getLast().getRow(), 
				walkingPath.getLast().getCol(), map);
		else graph = new GridGraph(character.getRow(), character.getCol(), map);
		Path newPath = graph.shortestPathTo(row, col);
		
		if(newPath != null)
		{
			if(walkingPath == null)
				walkingPath = newPath;
			else {
				walkingPath.catPath(newPath);
				walkingPath.optimize();
			}
			interrupted = false;
			
			/* Were we already walking? */
			if(arrived)
			{
				if(!manager.setReel("walk", true))
					throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
				manager.setAnimationSpeed(15);
			}
			
			arrived = false;
		}
	}

	@Override
	public void animation(double seconds) 
	{
		if(walkingPath == null) return;
		
		double tile_size = character.getCurrentMap().getTileSize();
		double speed = character.getWalkingSpeed();
		double charX = character.getX() + (speed * vector_x * seconds * tile_size);
		double charY = character.getY() + (speed * vector_y * seconds * tile_size);
		
		double destX = walkingPath.getNextCol() * tile_size;
		double destY = walkingPath.getNextRow() * tile_size;
		
		double diffX = destX - charX;
		double diffY = destY - charY;
		
		if(diffX > 0) diffX = 1;
		else if(diffX == 0) diffX = 0;
		else diffX = -1;
		if(diffY > 0) diffY = 1;
		else if(diffY == 0) diffY = 0;
		else diffY = -1;
		
		if(diffX != vector_x || diffY != vector_y)
		{
			/* We passed our destination */
			character.setX(destX);
			character.setY(destY);
			
			/* Let the quest engine know we changed our location */
			game.getQuestEngine().movedTo(character, 
					walkingPath.getNextRow(), walkingPath.getNextCol());
			
			/* Remove all points that are at this point */
			walkingPath.moveForward();
			
			/* Did we reach the destination? */
			if(walkingPath.isEmpty() || interrupted)
			{
				/* nullify the path */
				walkingPath = null;
				
				/* Should we end the animation? */
				if(!smooth)
				{
					/* Do the end walk animation */
					arrive();
					arrived = true;

					Iterator<FollowListener> it = character.getFollowers();
					while(it.hasNext())
						it.next().characterArrived(character); 
				}
				
				/* Let the listener know we got to the dest */
				if(listener != null)
					listener.animationFinished();
			} else {
				/* Change direction properties */
				int dir = this.lookTowards(walkingPath.getNextRow(), 
						walkingPath.getNextCol());
				manager.setReelDirection(dir);
				this.generateVectors(dir);
				
				Iterator<FollowListener> it = character.getFollowers();
				while(it.hasNext())
				{
					it.next().characterMoving(character, 
							character.getRow(), 
							character.getCol());
				}
			}
		} else {
			character.setX(charX);
			character.setY(charY);
		}
	}
	
	@Override
	public void directionChanged(int direction) 
	{
		generateVectors(direction);
	}
	
	private Path walkingPath;
	private boolean arrived;
	private boolean interrupted;
	private boolean smooth;
	
	private static final long serialVersionUID = 4015343237303860257L;
}
