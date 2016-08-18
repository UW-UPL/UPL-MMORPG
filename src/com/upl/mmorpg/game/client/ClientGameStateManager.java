package com.upl.mmorpg.game.client;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.server.GameStateInterface;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.map.Grid2DMap;
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
	public void updateCharacter(Object uuid, Object obj) 
	{
		if(obj instanceof MMOCharacter && uuid instanceof CharacterUUID)
		{
			MMOCharacter character = (MMOCharacter)obj;
			CharacterUUID cuuid = (CharacterUUID)uuid;
			if(!game.updateCharacter(cuuid, character))
				Log.e("Updating character failed!!");
			else Log.vln("Update character: " + character.getName());
		} else Log.e("Update character but wasn't sent character!");
	}

	@Override
	public void updateMap(int map_id, Object obj) 
	{
		if(obj instanceof Grid2DMap)
		{
			Grid2DMap map = (Grid2DMap)obj;
			game.updateMap(map);
		}
	}
	
	@Override
	public void itemDropped(int row, int col, Object obj) 
	{
		if(obj instanceof Item)
		{
			Item i = (Item)obj;
			game.itemDropped(row, col, i);
		}
	}

	@Override
	public void itemPickedUp(Object obj, Object obj2) 
	{
		if(obj instanceof Item && obj2 instanceof CharacterUUID)
		{
			Item i = (Item)obj;
			CharacterUUID uuid = (CharacterUUID)obj2;
			game.itemPickedUp(i.getUUID(), game.getCharacter(uuid));
		}
	}
    
	private RPCManager rpc;
	private ClientGame game;

}
