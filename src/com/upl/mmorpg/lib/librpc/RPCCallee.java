package com.upl.mmorpg.lib.librpc;

import com.upl.mmorpg.lib.StackBuffer;

public interface RPCCallee {
	public void invalid_rpc(int num);
	public StackBuffer handle_call(StackBuffer stack);
}
