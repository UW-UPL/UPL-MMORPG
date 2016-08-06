package com.upl.examplegame.pong.server;

import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class PongRPCCaller 
{
	public PongRPCCaller(RPCManager rpc)
	{
		this.rpc = rpc;
	}

	public void otherPlayerPaddle(float arg0, float arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		stack.pushFloat(arg0);
		stack.pushFloat(arg1);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void youScored()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(2);
		/* Push the arguments */
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void otherPlayerScored()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(3);
		/* Push the arguments */
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void setPuckProperties(float arg0, float arg1, float arg2, float arg3, float arg4)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(4);
		/* Push the arguments */
		stack.pushFloat(arg0);
		stack.pushFloat(arg1);
		stack.pushFloat(arg2);
		stack.pushFloat(arg3);
		stack.pushFloat(arg4);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void setScore(int arg0, int arg1)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(5);
		/* Push the arguments */
		stack.pushInt(arg0);
		stack.pushInt(arg1);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void setOpponentName(String arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(6);
		/* Push the arguments */
		stack.pushString(arg0);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void setPlayerNumber(int arg0)
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(7);
		/* Push the arguments */
		stack.pushInt(arg0);
		/* Do the network call */
		rpc.do_call(stack, false);
	}

	public void hidePuck()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(8);
		/* Push the arguments */
		/* Do the network call */
		rpc.do_call(stack, false);
	}
	
	private RPCManager rpc;
}
