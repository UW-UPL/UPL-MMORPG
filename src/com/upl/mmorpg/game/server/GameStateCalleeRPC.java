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

	public StackBuffer __requestCurrentMap(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		Object result = client.requestCurrentMap();
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushObject(result);
		return ret_stack;
	}

	public StackBuffer __requestCharacters(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		Object result = client.requestCharacters();
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushObject(result);
		return ret_stack;
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
		case 1: /** requestCurrentMap */
			result = __requestCurrentMap(stack);
			break;
		case 2: /** requestCharacters */
			result = __requestCharacters(stack);
			break;
		default:
			invalid_rpc(func_num);
			break;
		};

		return result;
	}
	
	private GameStateManager client;
}
