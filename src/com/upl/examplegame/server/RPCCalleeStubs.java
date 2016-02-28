package com.upl.examplegame.server;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;

public class RPCCalleeStubs implements RPCCallee
{
	public RPCCalleeStubs(ClientHandler client)
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
			case 1: /** broadcast_message */
				result = __broadcast_message(stack);
				break;
			case 2: /** register */
				result = __register(stack);
				break;
			case 3: /** echo */
				result = __echo(stack);
				break;
			default:
				invalid_rpc(func_num);
				break;
		};

		return result;
	}
	
	public StackBuffer __broadcast_message(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		boolean result = client.broadcast_message(arg0);
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
		boolean result = client.register(arg0);
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushBoolean(result);
		return ret_stack;
	}
	public StackBuffer __echo(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		boolean result = client.echo(arg0);
		/* Make a result stack */
		StackBuffer ret_stack = new StackBuffer();
		ret_stack.pushBoolean(result);
		return ret_stack;
	}
	
	private ClientHandler client;
}
