package com.upl.mmorpg.game.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Base class for Inventory, Bank, ect. This class can also be treated as a collection.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class ItemList implements Serializable, Collection<Item>
{
	public ItemList(int capacity)
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
	public boolean add(Item i)
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
	public boolean remove(int index)
	{
		boolean removed = (index >= 0 && index < items.size());
		items.remove(index);
		return removed;
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
	public Iterator<Item> iterator()
	{
		return items.iterator();
	}
	
	public boolean addAll(Collection<? extends Item> collection)
	{
		boolean changed = false;
		for(Item i : collection)
		{
			if(items.size() < capacity)
			{
				items.add(i);
				changed = true;
			} else throw new IllegalStateException();
			
		}
		
		return changed;
	}
	
	/** Collection functions */
	@Override public void clear() { items.clear();}
	@Override public boolean contains(Object arg0) { return items.contains(arg0); }
	@Override public boolean containsAll(Collection<?> c) { return items.containsAll(c); }
	@Override public boolean isEmpty() { return items.isEmpty(); }
	@Override public boolean remove(Object o) { return items.remove(o); }
	@Override public boolean removeAll(Collection<?> list) { return items.removeAll(list); }
	@Override public boolean retainAll(Collection<?> list) { return items.retainAll(list); }
	@Override public int size() { return items.size(); }
	@Override public Object[] toArray() { return items.toArray(); }
	@Override public <T> T[] toArray(T[] a){ return items.toArray(a); }

	private ArrayList<Item> items; /**< The list of items in the item list. */
	private int capacity; /**< The maximum number of items that can be in this list. */

	private static final long serialVersionUID = 2564882783628929318L;
}
