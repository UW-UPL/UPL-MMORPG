package com.upl.examplegame.pong.client;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class PongRPCCaller 
{
	public PongRPCCaller(RPCManager rpc)
	{
		this.rpc = rpc;
	}

	public void updatePaddle(int arg0, int arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		stack.pushInt(arg0);
		stack.pushInt(arg1);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void puckDeflected(float arg0, float arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(2);
		/* Push the arguments */
		stack.pushFloat(arg0);
		stack.pushFloat(arg1);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void setName(String arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(3);
		/* Push the arguments */
		stack.pushString(arg0);
		/* Do the network call */
		rpc.do_call(stack, false);
	}
	
	public void ready()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(4);
		/* Push the arguments */
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	private RPCManager rpc;
}
