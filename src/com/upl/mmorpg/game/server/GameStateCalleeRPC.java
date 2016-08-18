package com.upl.mmorpg.game.server;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;
import com.upl.mmorpg.lib.util.StackBuffer;

public class GameStateCalleeRPC implements RPCCallee
{
	public GameStateCalleeRPC(GameStateManager client)
	{
		this.client = client;
	}

	@Override
	public void invalid_rpc(int num)
	{
		Log.e("Invalid RPC used!");
	}

	public void __updateCharacter(StackBuffer stack)
	{
		/* Pop the arguments */
		Object arg0 = stack.popObject();
		Object arg1 = stack.popObject();

		/* Do the function call */
		client.updateCharacter(arg0, arg1);
	}

	public void __updateMap(StackBuffer stack)
	{
		/* Pop the arguments */
		int arg0 = stack.popInt();
		Object arg1 = stack.popObject();

		/* Do the function call */
		client.updateMap(arg0, arg1);
	}

	public void __itemDropped(StackBuffer stack)
	{
		/* Pop the arguments */
		int arg0 = stack.popInt();
		int arg1 = stack.popInt();
		Object arg2 = stack.popObject();

		/* Do the function call */
		client.itemDropped(arg0, arg1, arg2);
	}

	public void __itemPickedUp(StackBuffer stack)
	{
		/* Pop the arguments */
		Object arg0 = stack.popObject();
		Object arg1 = stack.popObject();

		/* Do the function call */
		client.itemPickedUp(arg0, arg1);
	}

	@Override
	public StackBuffer handle_call(StackBuffer stack)
	{
		/* Get the function number */
		int func_num = stack.popInt();
		/* We are expecting a result stack buffer */
		StackBuffer result = null;
		switch(func_num)
		{
		case 1: /** updateCharacter */
			__updateCharacter(stack);
			break;
		case 2: /** updateMap */
			__updateMap(stack);
			break;
		case 3: /** itemDropped */
			__itemDropped(stack);
			break;
		case 4: /** itemPickedUp */
			__itemPickedUp(stack);
			break;
		default:
			invalid_rpc(func_num);
			break;
		};

		return result;
	}


	private GameStateManager client;
}
