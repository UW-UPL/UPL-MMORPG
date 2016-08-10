package com.upl.mmorpg.lib.librpc;

import com.upl.mmorpg.lib.util.StackBuffer;

public interface RPCObject 
{
	/**
	 * Pushes the object into the StackBuffer. The class should also
	 * define a pullFromStack(StackBuffer buff) method that is static
	 * for creating objects from the stack.
	 * @param buff
	 */
	public void pushOntoStack(StackBuffer buff);
}
