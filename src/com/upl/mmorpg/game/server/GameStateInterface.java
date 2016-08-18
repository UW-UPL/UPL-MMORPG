package com.upl.mmorpg.game.server;

public interface GameStateInterface 
{
	/* Client -> Server */
	public Object requestCurrentMap();
	public Object requestCharacters();

	/* Server -> Client */
	public void updateCharacter(Object arg0, Object arg1);
	public void updateMap(int arg0, Object arg1);
	public void itemDropped(int arg0, int arg1, Object arg2);
	public void itemPickedUp(Object arg0, Object arg1);
}
