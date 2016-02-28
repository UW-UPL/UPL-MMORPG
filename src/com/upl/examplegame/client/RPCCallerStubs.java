package com.upl.examplegame.client;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class RPCCallerStubs 
{
	public RPCCallerStubs(RPCManager rpc)
	{
		this.rpc = rpc;
	}
	
	public boolean broadcast_message(String arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		stack.pushString(arg0);
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack);
		return res.popBoolean();
	}

	public boolean register(String arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(2);
		/* Push the arguments */
		stack.pushString(arg0);
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack);
		return res.popBoolean();
	}

	public boolean echo(String arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(3);
		/* Push the arguments */
		stack.pushString(arg0);
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack);
		return res.popBoolean();
	}
	
	private RPCManager rpc;
}
