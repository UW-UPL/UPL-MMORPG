package com.upl.mmorpg.game.client;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.server.GameStateInterface;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.game.uuid.ItemUUID;
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

	public Object requestPlayerUUID()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(3);
		/* Push the arguments */
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack, true);
		return res.popObject();
	}

	public boolean updateCharacter(Object arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(4);
		/* Push the arguments */
		stack.pushObject(arg0);
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack, true);
		return res.popBoolean();
	}


	/** Callee methods */
	@Override
	public void updateCharacter(Object uuid, Object obj) 
	{
		if(uuid instanceof CharacterUUID && obj instanceof MMOCharacter)
		{
			CharacterUUID cuuid = (CharacterUUID)uuid;
			MMOCharacter character = (MMOCharacter)obj;
			if(!game.updateCharacter(cuuid, character))
				Log.e("Updating character failed!!");
			else Log.vln("Update character: " + character.getName());
		} else Log.e("Update character but wasn't sent character  uuid: " 
				+ uuid + " character: " + obj);
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
	public void itemDropped(int row, int col, Object obj, Object obj2) 
	{
		if(obj instanceof ItemUUID && obj2 instanceof CharacterUUID)
		{
			ItemUUID i = (ItemUUID)obj;
			CharacterUUID uuid = (CharacterUUID)obj2;
			MMOCharacter character = game.getCharacter(uuid);
			if(character == null)
			{
				Log.e("Couldn't find character: " + uuid);
				return;
			}
			
			Item item = game.getItem(i);
			if(item == null)
			{
				Log.e("Couldn't find item: " + i);
				return;
			}

			game.itemDropped(row, col, item, character);
		} else Log.e("parameter issues with itemDropped on client");
	}

	@Override
	public void itemPickedUp(Object obj, Object obj2) 
	{
		if(obj instanceof ItemUUID && obj2 instanceof CharacterUUID)
		{
			ItemUUID i = (ItemUUID)obj;
			CharacterUUID uuid = (CharacterUUID)obj2;

			if(game.getCharacter(uuid) == null)
			{
				Log.e("I have no idea who character " + uuid + " is.");
				return;
			}

			if(!game.itemPickedUp(i, game.getCharacter(uuid)))
				Log.e("Player " + uuid + " can't pick that up!");
		} else Log.e("Bad arguments for itemPickedUp.");
	}

	private RPCManager rpc;
	private ClientGame game;
}
