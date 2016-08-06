package com.upl.mmorpg.game.item;

import java.util.ArrayList;
import java.util.Iterator;

import com.upl.mmorpg.lib.util.StackBuffer;
import com.upl.mmorpg.lib.util.StackBufferable;

/**
 * Base class for Inventory, Bank, ect.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class ItemList implements StackBufferable
{
	protected ItemList(int capacity)
	{
		this.capacity = capacity;
		items = new ArrayList<Item>(capacity);
	}
	
	/**
	 * Get the maximum amount of items that can go into the item list.
	 * @return The maximum capacity of the item list.
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * Returns the amount of items in the item list.
	 * @return The amount of items in the item list.
	 */
	public int getCount()
	{
		return items.size();
	}
	
	/**
	 * Add an item to the end of the item list.
	 * @param i The item to add to the item list.
	 * @return Whether or not the item could be added to the item list.
	 */
	public boolean addItem(Item i)
	{
		if(items.size() >= capacity)
			return false;
		items.add(new Item(i));
		return true;
	}
	
	/**
	 * Insert an item into this item list.
	 * @param i The item to insert.
	 * @param index The index to insert the item at.
	 * @return Whether or not the item could be inserted.
	 */
	public boolean insertItem(Item i, int index)
	{
		if(items.size() >= capacity)
			return false;
		items.add(index, new Item(i));
		return true;
	}
	
	/**
	 * Removes an item from the list.
	 * @param i The item to remove.
	 * @return Whether or not the item was in the list.
	 */
	public boolean removeItem(Item i)
	{
		return items.remove(i);
	}
	
	/**
	 * Removes all occurrances of the given object.
	 * @param i The item to remove.
	 * @return Whether or not any occurrences were found.
	 */
	public boolean removeAll(Item i)
	{
		boolean found = false;
		Iterator<Item> it = items.iterator();
		while(it.hasNext())
		{
			if(it.next().equals(i))
			{
				found = true;
				it.remove();
			}
		}
		
		return found;
	}
	
	/**
	 * Remove the item at the given index.
	 * @param i The index at which to remove the item.
	 * @return Whether or not the item could be removed.
	 */
	public boolean removeItem(int i)
	{
		if(i >= capacity)
			return false;
		return items.remove(i) != null;
	}
	
	/**
	 * Swap the items in at two indexes.
	 * @param idx1 The index of the first item.
	 * @param idx2 The index of the second item.
	 * @return Whether or not the items could be swapped.
	 */
	public boolean swapItems(int idx1, int idx2)
	{
		if(idx1 < 0 || idx1 >= capacity)
			return false;
		if(idx2 < 0 || idx2 >= capacity)
			return false;
		
		Item item1 = items.get(idx1);
		Item item2 = items.get(idx2);
		if(item1 == null || item2 == null)
			return false;
		
		items.set(idx1, item2);
		items.set(idx2, item1);
		return true;
	}
	
	/**
	 * Return an iterator for the item list.
	 * @return An iterator for the item list.
	 */
	protected Iterator<Item> iterator()
	{
		return items.iterator();
	}
	
	public StackBuffer pushToStackBuffer(StackBuffer buff)
	{
		buff.pushInt(capacity);
		buff.pushInt(items.size());
		Iterator<Item> it = items.iterator();
		
		while(it.hasNext()) 
		{
			Item item = it.next();
			if(item instanceof ItemStack)
				buff.pushBoolean(true);
			else buff.pushBoolean(false);
			
			item.pushToStackBuffer(buff);
		}
		
		return buff;
	}
	
	public StackBuffer popFromStackBuffer(StackBuffer buff)
	{
		items = new ArrayList<Item>();
		capacity = buff.popInt();
		int items_count = buff.popInt();
		for(int x = 0;x < items_count;x++)
		{
			if(buff.popBoolean())
				items.add(new ItemStack(buff));
			else items.add(new Item(buff));
		}
		
		return buff;
	}
	
	private ArrayList<Item> items; /**< The list of items in the item list. */
	private int capacity; /** The maximum number of items that can be in this list. */
}
