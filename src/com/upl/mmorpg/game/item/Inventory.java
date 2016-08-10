package com.upl.mmorpg.game.item;

import java.util.Iterator;

public class Inventory extends ItemList 
{
	public Inventory()
	{
		super(MAX_CAPACITY);
	}

	public int addItemStack(ItemStack stack)
	{
		for(int x = 0;x < stack.getCount();x++)
			if(!addItem(new Item(stack)))
				return x;
		
		return stack.getCount();
	}
	
	public boolean addItem(Item item)
	{
		/* If this is a stack, we need to know */
		ItemStack istack = null;
		if(item instanceof ItemStack)
		{
			istack = (ItemStack)item;
			int i = addItemStack(istack);
			/* Was there success? */
			if(i == istack.getCount())
				return true;
			
			/* Failure -- undo that operation */
			removeItemStack(new ItemStack(i, item));
			
			/* Return failure */
			return false;
		}
		
		Iterator<Item> it = iterator();

		int index = 0;
		while(it.hasNext())
		{
			/* Get the next item */
			Item i = it.next();
			if(i.getId() != item.getId())
				continue;

			/* Is this a stack? */
			if(i instanceof ItemStack)
			{
				/* See if this stack has room for another element */
				ItemStack stack = (ItemStack)i;
				if(stack.add(1))
					return true;
			} else if(i.getId() == item.getId())
			{
				/* Can we create a stack for this item? */
				ItemStack stack = ItemDef.stacks[i.getId()];

				/* Can we stack this item? */
				if(stack.setCount(2) == 2)
				{
					it.remove(); /* Remove the old object */

					/* Insert the new stack at the index */
					if(insertItem(stack, index))
						return true;
				} 
			}

			index++;
		}

		/* Just try to use the parent addItem */
		return super.addItem(item);
	}
	
	public int removeItemStack(ItemStack stack)
	{
		for(int x = 0;x < stack.getCount();x++)
			if(!removeItem(new Item(stack)))
				return x;
		
		return stack.getCount();
	}
	
	public boolean removeItem(Item item)
	{
		Iterator<Item> it = iterator();
		
		ItemStack istack = null;
		if(item instanceof ItemStack)
		{
			istack = (ItemStack)item;
			
			/* TODO Failure here? */
			return removeItemStack((ItemStack)item) == istack.getCount();
		}
		
		while(it.hasNext())
		{
			Item i = it.next();
			
			/* Do these ids match? */
			if(i.getId() != item.getId())
				continue;
			
			/* Is this an item stack? */
			if(i instanceof ItemStack)
			{
				ItemStack stack = (ItemStack)i;
				if(stack.getCount() > 1)
				{
					if(stack.sub(1))
						return true;
				} else if(stack.getCount() == 1)
				{
					it.remove();
					return true;
				}
			} else {
				it.remove();
				return true;
			}
		}
		
		return false;
	}

	private static final int MAX_CAPACITY = 20;
	
	private static final long serialVersionUID = -6923402125333683975L;
}
