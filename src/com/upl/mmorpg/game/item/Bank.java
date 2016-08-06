package com.upl.mmorpg.game.item;

/**
 * Represents a user's Bank. The bank is a collection of
 * items a user has stored in their bank account.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class Bank extends ItemList 
{
	public Bank()
	{
		super(MAX_CAPACITY);
	}
	
	private static final int MAX_CAPACITY = 128;
}
