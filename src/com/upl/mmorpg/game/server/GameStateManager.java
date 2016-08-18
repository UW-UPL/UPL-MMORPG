package com.upl.mmorpg.game.server;

import java.io.IOException;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.game.uuid.ItemUUID;
import com.upl.mmorpg.lib.liblog.Log;
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

	@Override
	public Object requestPlayerUUID() 
	{
		return character.getUUID();
	}

	@Override
	public boolean updateCharacter(Object obj) 
	{
		if(obj instanceof MMOCharacter)
		{
			MMOCharacter character = (MMOCharacter)obj;
			if(character.getUUID().equals(this.character.getUUID()))
			{
				try 
				{
					this.character.update(character);
					
					/* propegate this update */
					game.characterUpdated(this.character, false);
					return true;
				} catch (IOException e) 
				{
					Log.wtf("Couldn't update client's character!", e);
				}
			} else Log.e("UUID mismatch!");
		}

		return false;
	}

	/** Caller methods */
	public void updateCharacter(Object arg0, Object arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		stack.pushObject(arg0);
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

	public void itemDropped(int arg0, int arg1, Object arg2, Object arg3)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(3);
		/* Push the arguments */
		stack.pushInt(arg0);
		stack.pushInt(arg1);
		stack.pushObject(arg2);
		stack.pushObject(arg3);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void itemPickedUp(Object arg0, Object arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(4);
		/* Push the arguments */
		stack.pushObject(arg0);
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
	
	public CharacterUUID getPlayerUUID()
	{
		return character.getUUID();
	}
	
	public MMOCharacter getPlayer()
	{
		return character;
	}
	
	@Override
	public void requestdropItem(int row, int col, Object obj) 
	{
		if(obj instanceof ItemUUID)
		{
			ItemUUID uuid = (ItemUUID)obj;
			Item item = game.getItem(uuid);
			if(item == null)
			{
				Log.e("I don't know where this item is: " + uuid);
				return;
			}
			
			game.dropItem(character, item);
		}
	}

	@Override
	public void requestPickUpItem(int row, int col, Object obj) 
	{
		if(obj instanceof ItemUUID)
		{
			ItemUUID uuid = (ItemUUID)obj;
			Item item = game.getItem(uuid);
			if(item == null)
			{
				Log.e("I don't know where this item is: " + uuid);
				return;
			}
			
			game.pickupItem(character, item);
		}
	}

	private ServerGame game;
	private MMOCharacter character;
	private RPCManager rpc;
}
