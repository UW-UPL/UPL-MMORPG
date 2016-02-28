package com.upl.examplegame.client;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;

public class RPCCalleeStubs implements RPCCallee
{
	public RPCCalleeStubs(ExampleClient client)
	{
		this.client = client;
	}
	
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
			case 1: /** receive_message */
				result = __receive_message(stack);
				break;
			default:
				invalid_rpc(func_num);
				break;
		};

		return result;
	}
	
	public StackBuffer __receive_message(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();
		String arg1 = stack.popString();

		/* Do the function call */
		boolean result = client.receive_message(arg0, arg1);
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushBoolean(result);
		return ret_stack;
	}
	
	private ExampleClient client;
}
