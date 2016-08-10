package com.upl.mmorpg.lib.librpc;

import com.upl.mmorpg.lib.util.StackBuffer;

public interface RPCCallee 
{
	/**
	 * Called when a request for an invalid function is received.
	 * @param num The invalid function number.
	 */
	public void invalid_rpc(int num);
	
	/**
	 * Handles a request for a remote procedure call.
	 * @param stack The StackBuffer that was received for the
	 * 			request.
	 * @return A resulting StackBuffer to be sent back.
	 */
	public StackBuffer handle_call(StackBuffer stack);
}
