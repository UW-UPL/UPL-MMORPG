package com.upl.examplegame.pong.client;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;

public class PongRPCCallee implements RPCCallee
{
	public PongRPCCallee(ClientGame client)
	{
		this.client = client;
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
			case 1: /** otherPlayerPaddle */
				__otherPlayerPaddle(stack);
				break;
			case 2: /** youScored */
				__youScored(stack);
				break;
			case 3: /** otherPlayerScored */
				__otherPlayerScored(stack);
				break;
			case 4: /** setPuckProperties */
				__setPuckProperties(stack);
				break;
			case 5: /** setScore */
				__setScore(stack);
				break;
			case 6: /** setOpponentName */
				__setOpponentName(stack);
				break;
			case 7: /** setPlayerNumber */
				__setPlayerNumber(stack);
				break;
			case 8: /** hidePuck */
				__hidePuck(stack);
				break;
			default:
				invalid_rpc(func_num);
				break;
		};

		return result;
	}

	@Override
	public void invalid_rpc(int num) 
	{
		Log.e("Invalid RPC used!");
	}

	public void __otherPlayerPaddle(StackBuffer stack)
	{
		/* Pop the arguments */
		float arg0 = stack.popFloat();
		float arg1 = stack.popFloat();

		/* Do the function call */
		client.otherPlayerPaddle(arg0, arg1);
	}

	public void __youScored(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		client.youScored();
	}

	public void __otherPlayerScored(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		client.otherPlayerScored();
	}

	public void __setPuckProperties(StackBuffer stack)
	{
		/* Pop the arguments */
		float arg0 = stack.popFloat();
		float arg1 = stack.popFloat();
		float arg2 = stack.popFloat();
		float arg3 = stack.popFloat();
		float arg4 = stack.popFloat();

		/* Do the function call */
		client.setPuckProperties(arg0, arg1, arg2, arg3, arg4);
	}

	public void __setScore(StackBuffer stack)
	{
		/* Pop the arguments */
		int arg0 = stack.popInt();
		int arg1 = stack.popInt();

		/* Do the function call */
		client.setScore(arg0, arg1);
	}

	public void __setOpponentName(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		client.setOpponentName(arg0);
	}

	public void __setPlayerNumber(StackBuffer stack)
	{
		/* Pop the arguments */
		int arg0 = stack.popInt();

		/* Do the function call */
		client.setPlayerNumber(arg0);
	}

	public void __hidePuck(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		client.hidePuck();
	}

	private ClientGame client;
}
