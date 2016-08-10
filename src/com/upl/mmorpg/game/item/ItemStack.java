package com.upl.mmorpg.game.item;

/**
 * Represents a stack of items on the ground or in an
 * inventory or bank. The capacity of the stack is the
 * maximum amount of items that can go into this stack.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class ItemStack extends Item
{
	public ItemStack(int capacity, Item item)
	{
		super(item);
		this.capacity = capacity;
	}
	
	/**
	 * Returns the maximum amount of items that can go into this stack.
	 * @return The maximum amount of items that can go into this stack.
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * Add the amount of items to the stack.
	 * @param items The amount of items to add to this stack.
	 * @return Wether or not that many items could be added.
	 */
	public boolean add(int items)
	{
		if(items < 0) return false;
		count += items;
		
		if(count > capacity)
			count = capacity;
		else return true;
		
		return false;
	}
	
	/**
	 * Remove the amount of items from this stack.
	 * @param items The amount of items to subtract.
	 * @return Whether or not that many items could be subtracted
	 */
	public boolean sub(int items)
	{
		if(items > 0) return false;
		count -= items;
		
		if(count < 0)
			count = 0;
		else return true;

		return false;
	}
	
	/**
	 * Set the amount of items in this stack.
	 * @param items The amount of items.
	 * @return The new amount of items.
	 */
	public int setCount(int items)
	{
		count = items;
		if(count < 0)
			count = 0;
		if(count > capacity)
			count = capacity;
		
		return count;
	}
	
	/**
	 * Returns the amount of items in this stack.
	 * @return The amount of items in this stack.
	 */
	public int getCount()
	{
		return count;
	}
	
	private final int capacity; /**< How many items can be in this stack? */
	private int count; /**< How many items are in this stack? */
	
	private static final long serialVersionUID = -7754169467210636245L;
}
