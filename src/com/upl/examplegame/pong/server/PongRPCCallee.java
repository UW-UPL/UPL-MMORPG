package com.upl.examplegame.pong.server;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;

public class PongRPCCallee implements RPCCallee
{
	public PongRPCCallee(NetworkPlayer client)
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
			case 1: /** updatePaddle */
				__updatePaddle(stack);
				break;
			case 2: /** puckDeflected */
				__puckDeflected(stack);
				break;
			case 3: /** setName */
				__setName(stack);
				break;
			case 4: /** ready */
				__ready(stack);
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

	public void __updatePaddle(StackBuffer stack)
	{
		/* Pop the arguments */
		int arg0 = stack.popInt();
		int arg1 = stack.popInt();

		/* Do the function call */
		client.updatePaddle(arg0, arg1);
	}

	public void __puckDeflected(StackBuffer stack)
	{
		/* Pop the arguments */
		float arg0 = stack.popFloat();
		float arg1 = stack.popFloat();

		/* Do the function call */
		client.puckDeflected(arg0, arg1);
	}

	public void __setName(StackBuffer stack)
	{
		/* Pop the arguments */
		String arg0 = stack.popString();

		/* Do the function call */
		client.setName(arg0);
	}
	
	public void __ready(StackBuffer stack)
	{
		/* Pop the arguments */

		/* Do the function call */
		client.ready();
	}

	private NetworkPlayer client;
}
