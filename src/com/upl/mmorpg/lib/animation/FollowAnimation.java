package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.FollowListener;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.GridPoint;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class FollowAnimation extends Animation 
		implements FollowListener, AnimationListener
{
	public FollowAnimation(Game game, AnimationManager manager, MMOCharacter character,
			Grid2DMap map, AnimationListener listener, int duration)
	{
		super(game, manager, character, listener, duration);

		this.map = map;
		isMoving = false;
		animating = false;
		this.following = null;
		this.followingIsMoving = false;
	}
	
	public void setFollee(MMOCharacter following)
	{
		this.following = following;
	}

	@Override
	public void animationInterrupted(Animation source) 
	{
		/* This was an external interrupt */
		isMoving = false;
		animating = false;
		following.removeFollower(this);
	}
	
	@Override
	public void animationStarted() 
	{
		isMoving = false;
		animating = true;
		following.addFollower(this);
		
		GridPoint behind = following.getBehindPoint();
		walkTo(behind.getRow(), behind.getColumn());
		this.nextRow = behind.getRow();
		this.nextCol = behind.getColumn();
		//walking.animationStarted();
	}
	
	private void walkTo(int row, int col)
	{
		isMoving = true;
		//GridGraph graph = new GridGraph(character.getRow(), 
		//		character.getColumn(), map);
		//Path p = graph.shortestPathTo(row, col);
		//walking.setPath(p);
	}

	@Override
	public void animationReelFinished() 
	{
		//if(isMoving)
		//	walking.animationReelFinished();
		//else idle.animationReelFinished();
	}

	@Override
	public void animation(double seconds) 
	{
		//if(isMoving)
		//	walking.animation(seconds);
		//else idle.animation(seconds);
	}

	@Override
	public void directionChanged(int direction) 
	{
		//if(isMoving)
		//	walking.directionChanged(direction);
		//else idle.directionChanged(direction);
	}
	
	@Override
	public void characterMoving(MMOCharacter c, int dstRow, int dstCol) 
	{
		if(!animating) return;
		
		followingIsMoving = true;
		isMoving = true;
		
		/* Make sure not to play the end walk animation */
		//walking.setSmooth(true);
		
		nextRow = dstRow;
		nextCol = dstCol;
		
		//walking.appendWaypoint(dstRow, dstCol, map);
	}
	
	@Override
	public void characterArrived(MMOCharacter c) 
	{
		followingIsMoving = false;
		//walking.setSmooth(false);
	}
	
	@Override
	public void animationFinished(Animation animation) 
	{
		if(animating)
		{
			if(!followingIsMoving)
			{
				//walking.setSmooth(false);
				//walking.arrive();
				return;
			}
			
			if(character.getRow() == nextRow 
					&& character.getColumn() == nextCol)
				return;
		}
	}

	private MMOCharacter following;
	private Grid2DMap map;
	private int nextRow;
	private int nextCol;

	private boolean animating;
	private boolean isMoving;
	private boolean followingIsMoving;
	
	private static final long serialVersionUID = -3127413296635186049L;
}