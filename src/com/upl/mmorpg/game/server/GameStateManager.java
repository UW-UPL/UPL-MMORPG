package com.upl.mmorpg.game.server;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.util.StackBuffer;

public class GameStateManager implements GameStateInterface
{
	public GameStateManager(ServerGame game, MMOCharacter character, RPCManager rpc)
	{
		this.game = game;
		this.character = character;
		this.rpc = rpc;
		
		/* Set the new remote procedure call callee */
		rpc.setCallee(new GameStateCalleeRPC(this));
	}
	
	/** Callee methods */
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
	
	/** Caller methods */
    public void updateCharacter(int arg0, Object arg1)
    {
            StackBuffer stack = new StackBuffer();

            /* Push the function number */
            stack.pushInt(1);
            /* Push the arguments */
            stack.pushInt(arg0);
            stack.pushObject(arg1);
            /* Do the network call */
            rpc.do_call(stack, false);
    }

    public void updateMap(int arg0, Object arg1)
    {
            StackBuffer stack = new StackBuffer();

            /* Push the function number */
            stack.pushInt(2);
            /* Push the arguments */
            stack.pushInt(arg0);
            stack.pushObject(arg1);
            /* Do the network call */
            rpc.do_call(stack, false);
    }
	
    public int getCurrentMapID()
    {
    	return character.getCurrentMapID();
    }
    
    public Grid2DMap getCurrentMap()
    {
    	return character.getCurrentMap();
    }
    
	private ServerGame game;
	private MMOCharacter character;
	private RPCManager rpc;
}
