package com.upl.mmorpg.lib.animation;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.FollowListener;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class FollowAnimation extends Animation implements FollowListener
{
	public FollowAnimation(Game game, AnimationManager manager, MMOCharacter character,
			Grid2DMap map, int duration)
	{
		super(game, manager, character, duration);

		this.map = map;
		isMoving = false;
		animating = false;
		this.following = null;
		this.followingIsMoving = false;
	}
	
	public void setFollee(MMOCharacter following)
	{
		//this.following = following;
	}

	@Override
	public void interrupt(){}
	{
		/* This was an external interrupt */
		isMoving = false;
		animating = false;
		//following.removeFollower(this);
	}
	
	@Override
	public void animationStarted() 
	{
		isMoving = false;
		animating = true;
		//following.addFollower(this);
		
		//GridPoint behind = following.getBehindPoint();
		//walkTo(behind.getRow(), behind.getColumn());
		//this.nextRow = behind.getRow();
		//this.nextCol = behind.getColumn();
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
	
	// @Override
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
	
	@Override
	public void animationStopped() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString()
	{
		return "Follow Animation";
	}

	private CharacterUUID following;
	private Grid2DMap map;
	private int nextRow;
	private int nextCol;

	private boolean animating;
	private boolean isMoving;
	private boolean followingIsMoving;
	
	private static final long serialVersionUID = -3127413296635186049L;
}