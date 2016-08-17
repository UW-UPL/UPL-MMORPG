package com.upl.mmorpg.game.client;

import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class ClientGameStateManager 
{
	public ClientGameStateManager(ClientGame game, RPCManager rpc)
	{
		this.game = game;
		this.rpc = rpc;
	}

	public Object requestCurrentMap()
	{
		StackBuffer stack = new StackBuffer();

		/* Push the function number */
		stack.pushInt(1);
		/* Push the arguments */
		/* Do the network call */
		StackBuffer res = rpc.do_call(stack, true);
		return res.popObject();
	}

	private RPCManager rpc;
	private ClientGame game;
}
