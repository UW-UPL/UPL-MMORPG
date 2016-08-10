package com.upl.mmorpg.lib.util;

/**
 * Interface for objects that can be pushed into or poped from a
 * stack buffer.
 * 
 * @author John <jdetter@wisc.edu>
 *
 */

public interface StackBufferable
{
	/**
	 * Push an object into the stack.
	 * @param buff The stack buffer to push the object into.
	 * @return The original stack buffer.
	 */
	public StackBuffer pushToStackBuffer(StackBuffer buff);
	
	/**
	 * Pop an object from the stack buffer.
	 * @param buff The buffer to pop the object from.
	 * @return The original StackBuffer.
	 */
	public StackBuffer popFromStackBuffer(StackBuffer buff);
}
