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
			Grid2DMap map, double tile_size, AnimationListener listener)
	{
		super(game, manager, character, tile_size, listener);

		this.map = map;
		walking = new WalkingAnimation(game, manager, character, tile_size, this);
		idle = new IdleAnimation(game, manager, character, tile_size, null);
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
		if(source == walking || source == idle) return;
		
		/* This was an external interrupt */
		walking.animationInterrupted(source);
		idle.animationInterrupted(source);
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
		walkTo(behind.getRow(), behind.getCol());
		this.nextRow = behind.getRow();
		this.nextCol = behind.getCol();
		walking.animationStarted();
	}
	
	private void walkTo(int row, int col)
	{
		isMoving = true;
		GridGraph graph = new GridGraph(character.getRow(), 
				character.getCol(), map);
		Path p = graph.shortestPathTo(row, col);
		walking.setPath(p);
	}

	@Override
	public void animationReelFinished() 
	{
		if(isMoving)
			walking.animationReelFinished();
		else idle.animationReelFinished();
	}

	@Override
	public void animation(double seconds) 
	{
		if(isMoving)
			walking.animation(seconds);
		else idle.animation(seconds);
	}

	@Override
	public void directionChanged(int direction) 
	{
		if(isMoving)
			walking.directionChanged(direction);
		else idle.directionChanged(direction);
	}
	
	@Override
	public void characterMoving(MMOCharacter c, int dstRow, int dstCol) 
	{
		if(!animating) return;
		
		followingIsMoving = true;
		isMoving = true;
		
		/* Make sure not to play the end walk animation */
		walking.setSmooth(true);
		
		nextRow = dstRow;
		nextCol = dstCol;
		
		walking.appendWaypoint(dstRow, dstCol, map);
	}
	
	@Override
	public void characterArrived(MMOCharacter c) 
	{
		followingIsMoving = false;
		walking.setSmooth(false);
	}
	
	@Override
	public void animationFinished() 
	{
		if(animating)
		{
			if(!followingIsMoving)
			{
				walking.setSmooth(false);
				walking.arrive();
				return;
			}
			
			if(character.getRow() == nextRow 
					&& character.getCol() == nextCol)
				return;
		}
	}

	private WalkingAnimation walking;
	private IdleAnimation idle;
	private Grid2DMap map;
	private int nextRow;
	private int nextCol;

	private boolean animating;
	private boolean isMoving;
	private boolean followingIsMoving;
	private MMOCharacter following;
}