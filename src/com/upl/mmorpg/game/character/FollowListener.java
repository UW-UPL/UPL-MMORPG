package com.upl.mmorpg.game.character;

public interface FollowListener 
{
	public void characterMoving(MMOCharacter c, int dstRow, int dstCol);
}
