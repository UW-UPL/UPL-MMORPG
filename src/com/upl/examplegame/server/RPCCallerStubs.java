package com.upl.examplegame.server;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class RPCCallerStubs 
{
	public RPCCallerStubs(RPCManager rpc)
	{
		this.rpc = rpc;
	}
	
	public boolean receive_message(String arg0, String arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		stack.pushString(arg0);
		stack.pushString(arg1);
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack);
		return res.popBoolean();
	}
	
	private RPCManager rpc;
}
