package com.upl.mmorpg.game.character;

public interface FollowListener 
{
	public void characterMoving(MMOCharacter c, int lastRow, int lastCol);
	public void characterArrived(MMOCharacter c);
}
