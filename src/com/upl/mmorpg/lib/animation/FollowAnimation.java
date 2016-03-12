package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.character.FollowListener;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class FollowAnimation extends Animation implements FollowListener, AnimationListener
{
	public FollowAnimation(AnimationManager manager, MMOCharacter character,
			Grid2DMap map, double tile_size, AnimationListener listener)
	{
		super(manager, character, tile_size, listener);

		this.map = map;
		walking = new WalkingAnimation(manager, character, tile_size, this);
		idle = new IdleAnimation(manager, character, tile_size, null);
		isFollowing = false;
		animating = false;
		this.following = null;
	}
	
	public void setFollee(MMOCharacter following)
	{
		this.following = following;
	}

	@Override
	public void animationInterrupted(Animation source) 
	{
		if(source == walking || source == idle) return;
		
		System.out.println("Follow animation stopped");
		/* This was an external interrupt */
		walking.animationInterrupted(source);
		idle.animationInterrupted(source);
		isFollowing = false;
		animating = false;
		following.removeFollower(this);
	}

	@Override
	public void animationStarted() 
	{
		System.out.println("Following animation started");
		isFollowing = false;
		animating = true;
		following.addFollower(this);
	}
	
	private void walkTo(int row, int col)
	{
		if(animating && !isFollowing)
		{
			System.out.println("Walking to: " + row + " " + col);
			isFollowing = true;
			GridGraph graph = new GridGraph(character.getRow(), 
					character.getCol(), map);
			Path p = graph.shortestPathTo(row, col);
			walking.setPath(p);
			manager.setAnimation(walking);
		}
	}

	@Override
	public void animationReelFinished() 
	{
		if(isFollowing)
			walking.animationReelFinished();
		else idle.animationReelFinished();
	}

	@Override
	public void animation(double seconds) 
	{
		if(isFollowing)
			walking.animation(seconds);
		else idle.animation(seconds);
	}

	@Override
	public void directionChanged(int direction) 
	{
		if(isFollowing)
			walking.directionChanged(direction);
		else idle.directionChanged(direction);
	}
	
	@Override
	public void characterMoving(MMOCharacter c, int dstRow, int dstCol) 
	{
		if(!animating) return;
		System.out.println("Character moved!");
		
		nextRow = dstRow;
		nextCol = dstCol;
		
		if(isFollowing)
		{
			walking.interruptPath();
		} else {
			walkTo(dstRow, dstCol);
		}
	}
	
	@Override
	public void animationFinished() 
	{
		if(animating)
		{
			if(character.getRow() == nextRow 
					&& character.getCol() == nextCol)
				return;
				
			isFollowing = false;
			walkTo(nextRow, nextCol);
		}
	}

	private WalkingAnimation walking;
	private IdleAnimation idle;
	private Grid2DMap map;
	private int nextRow;
	private int nextCol;

	private boolean animating;
	private boolean isFollowing;
	private MMOCharacter following;
}