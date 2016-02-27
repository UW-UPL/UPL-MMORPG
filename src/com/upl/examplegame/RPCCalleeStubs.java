package com.upl.examplegame;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class RPCCalleeStubs implements RPCCallee
{
	@Override
	public void invalid_rpc(int num) 
	{
		Log.e("Invalid RPC used!");
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
			case 1: /** broadcast_message */
				result = __broadcast_message(stack);
				break;
			case 2: /** register */
				result = __register(stack);
				break;
			default:
				invalid_rpc(func_num);
				break;
		};

		return result;
	};
	
	public StackBuffer __broadcast_message(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		boolean result = game.broadcast_message(player_info, arg0);
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushBoolean(result);
		return ret_stack;
	}
	public StackBuffer __register(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		boolean result = game.register(arg0);
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushBoolean(result);
		return ret_stack;
	}
	
	private ExampleServer game;
	private String player_info;
}
