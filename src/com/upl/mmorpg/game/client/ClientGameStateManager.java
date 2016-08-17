package com.upl.mmorpg.game.client;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.server.GameStateInterface;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class ClientGameStateManager implements GameStateInterface
{
	public ClientGameStateManager(ClientGame game, RPCManager rpc)
	{
		this.game = game;
		this.rpc = rpc;
	}

	/** Caller methods */
    public Object requestCurrentMap()
    {
            StackBuffer stack = new StackBuffer();

            /* Push the function number */
            stack.pushInt(1);
            /* Push the arguments */
            /* Do the network call */
            StackBuffer res = rpc.do_call(stack, true);
            return res.popObject();
    }

    public Object requestCharacters()
    {
            StackBuffer stack = new StackBuffer();

            /* Push the function number */
            stack.pushInt(2);
            /* Push the arguments */
            /* Do the network call */
            StackBuffer res = rpc.do_call(stack, true);
            return res.popObject();
    }
    
    /** Callee methods */
	@Override
	public void updateCharacter(int entity_id, Object obj) 
	{
		if(obj instanceof MMOCharacter)
		{
			MMOCharacter character = (MMOCharacter)obj;
			if(!game.updateCharacter(character, entity_id))
				Log.e("Updating character failed!!");
			else Log.vln("Update character: " + character.getName());
		} else Log.e("Update character but wasn't sent character!");
	}

	@Override
	public void updateMap(int arg0, Object arg1) 
	{
		
	}
    
	private RPCManager rpc;
	private ClientGame game;
}
