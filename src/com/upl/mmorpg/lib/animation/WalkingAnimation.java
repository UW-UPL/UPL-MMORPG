package com.upl.mmorpg.lib.animation;

import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(Game game, AnimationManager animation, 
			MMOCharacter character, AnimationListener listener,
			int destRow, int destCol)
	{
		super(game, animation, character, listener, 1);
		this.vector_x = 0;
		this.vector_y = 0;
		this.walkingPath = null;
		this.destRow = destRow;
		this.destCol = destCol;
		arrived = false;
		interrupted =  false;
	}

	public void setPath(Path path)
	{
		if(path == null) return;
		walkingPath = path;
		interrupted = false;

		/* If we're at the first point, remove it */
		int myRow = character.getRow();
		int myCol = character.getColumn();

		while(walkingPath.getNextCol() == myCol 
				&& walkingPath.getNextRow() == myRow)
			walkingPath.moveForward();

		this.destRow = path.getLast().getRow();
		this.destCol = path.getLast().getColumn();
	}

	public void setDestination(int row, int col)
	{
		this.destRow = row;
		this.destCol = col;
	}

	public Path getPath()
	{
		if(walkingPath != null) 
			return walkingPath.copy();
		else return null;
	}

	@Override
	public void animationInterrupted(Animation animation) 
	{
		vector_x = 0;
		vector_y = 0;
	}

	@Override
	public void animationStarted() 
	{
		/**
		 * Do we need to calculate the walking path?
		 */
		if(walkingPath == null) 
		{
			Log.vln("Calculating path...");
			int startRow = character.getRow();
			int startCol = character.getColumn();

			GridGraph graph = new GridGraph(startRow, startCol, character.getCurrentMap());
			walkingPath = graph.shortestPathTo(destRow, destCol);
			if(walkingPath == null)
				return;
			Log.vln("Path calculated.");
		}

		Log.vln("Starting walk animation...");
		arrived = false;
		interrupted = false;
		if(!manager.setReel("walk_start", false))
			throw new RuntimeException("WALK ANIMATION NOT SUPPORTED");
		manager.setAnimationSpeed(100);

		int dir = lookTowards(walkingPath.getNextRow(), walkingPath.getNextCol());
		manager.setReelDirection(dir);
		this.generateVectors(dir);

		Iterator<CharacterUUID> it = character.getFollowers();
		while(it.hasNext())
		{
			//it.next().characterMoving(character, 
					//character.getRow(), 
					//character.getColumn());
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
					walkingPath.getLast().getColumn(), map);
		else graph = new GridGraph(character.getRow(), character.getColumn(), map);
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
		if(vector_x == 0.0d && vector_y == 0.0d)
			return;
		
		if(walkingPath == null) 
		{
			manager.nextAnimation();
			vector_x = 0.0d;
			vector_y = 0.0d;
			return;
		}

		double speed = character.getWalkingSpeed();
		double charX = character.getX() + (speed * vector_x * seconds);
		double charY = character.getY() + (speed * vector_y * seconds);

		double destX = walkingPath.getNextCol();
		double destY = walkingPath.getNextRow();

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

					//Iterator<FollowListener> it = character.getFollowers();
					//while(it.hasNext())
						//it.next().characterArrived(character); 
				}

				/* Let the listener know we got to the dest */
				if(listener != null)
					listener.animationFinished(this);
			} else {
				/* Change direction properties */
				int dir = lookTowards(walkingPath.getNextRow(), 
						walkingPath.getNextCol());
				manager.setReelDirection(dir);
				this.generateVectors(dir);

				//Iterator<FollowListener> it = character.getFollowers();
				//while(it.hasNext())
				//{
					//it.next().characterMoving(character, 
							//character.getRow(), 
							//character.getColumn());
				//}
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
	
	@Override
	public String toString()
	{
		return "Walking animation";
	}

	private Path walkingPath;
	private boolean arrived;
	private boolean interrupted;
	private boolean smooth;

	private int destRow;
	private int destCol;

	private static final long serialVersionUID = 4015343237303860257L;
}
