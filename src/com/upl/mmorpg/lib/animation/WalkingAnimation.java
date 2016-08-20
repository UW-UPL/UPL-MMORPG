package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.liblog.Log;

public class WalkingAnimation extends Animation
{
	public WalkingAnimation(Game game, AnimationManager animation, 
			MMOCharacter character, int destRow, int destCol)
	{
		super(game, animation, character, 1);
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
	public void interrupt(){}
	{
		vector_x = 0;
		vector_y = 0;
	}

	@Override
	public void animationStarted() 
	{
		/**
		 * Do we even need to go anywhere?
		 */
		if(character.getRow() == destRow && character.getColumn() == destCol)
		{
			/* Skip this animation */
			Log.vln("Character has skipped walking animation.");
			manager.nextAnimation();
			return;
		}

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
	}

	@Override
	public void animationReelFinished() 
	{
		if(arrived)
			manager.nextAnimation();
		else Log.e("Why did our walk animation end?");
	}

	public void interruptPath()
	{
		interrupted = true;
	}

	public void arrive()
	{
		manager.setReel("walk_end", false);
		manager.setAnimationSpeed(30);
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

		/* Calculate our new position */
		double speed = character.getWalkingSpeed();
		double charX = character.getX() + (speed * vector_x * seconds);
		double charY = character.getY() + (speed * vector_y * seconds);

		/* Calculate where we are supposed to be going */
		double destX = walkingPath.getNextCol();
		double destY = walkingPath.getNextRow();

		/* Calculate the direction we are supposed to be headed */
		double diffX = destX - charX;
		double diffY = destY - charY;

		/* normalize the direction vectors to either -1, 0, or 1 */
		if(diffX > 0) diffX = 1;
		else if(diffX < 0) diffX = -1;
		
		if(diffY > 0) diffY = 1;
		else if(diffY < 0) diffY = -1;

		/* Did we pass our destination? */
		if(diffX != vector_x || diffY != vector_y)
		{
			/* We passed our destination */
			character.setX(destX);
			character.setY(destY);

			/* Let the quest engine know we changed our location */
			game.getQuestEngine().movedTo(character, 
					walkingPath.getNextRow(), walkingPath.getNextCol());

			/* Get the next point we should move to */
			walkingPath.moveForward();

			/* Did we reach the destination? */
			if(walkingPath.isEmpty() || interrupted)
			{
				/* nullify the path */
				walkingPath = null;

				/* Do the end walk animation */
				arrived = true;
				arrive();

				//Iterator<FollowListener> it = character.getFollowers();
				//while(it.hasNext())
				//it.next().characterArrived(character); 
			} else {
				/* Change direction properties */
				int dir = lookTowards(walkingPath.getNextRow(), 
						walkingPath.getNextCol());
				manager.setReelDirection(dir);
				generateVectors(dir);

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
	public String toString()
	{
		return "Walking animation";
	}

	private Path walkingPath;
	private boolean arrived;
	private boolean interrupted;

	private int destRow;
	private int destCol;

	private static final long serialVersionUID = 4015343237303860257L;
}
