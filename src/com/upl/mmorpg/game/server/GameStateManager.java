package com.upl.mmorpg.game.server;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class GameStateManager implements GameStateInterface
{
	public GameStateManager(ServerGame game, MMOCharacter character, RPCManager rpc)
	{
		this.game = game;
		this.character = character;
		
		/* Set the new remote procedure call callee */
		rpc.setCallee(new GameStateCallee(this));
	}
	
	@Override
	public Object requestCurrentMap() 
	{
		return character.getCurrentMap();
	}
	
	@Override
	public Object requestCharacters() 
	{
		return game.getCharactersOnMap(character.getCurrentMapID());
	}
	
	private ServerGame game;
	private MMOCharacter character;

}
