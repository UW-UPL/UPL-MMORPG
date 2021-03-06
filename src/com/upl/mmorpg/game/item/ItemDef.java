package com.upl.mmorpg.game.item;

import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;

/**
 * Global item definitions. These are used to create new
 * representations of items and item stacks.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public final class ItemDef 
{
	/** Item IDs of all of the items */
	public static final Item WEAPON = new Item(1, Item.Type.GENERAL, "Weapon", "assets/images/items/weapons/test_weapon.png", 100);
	public static final int ARMOR = 2;
	public static final int IRON_ORE = 3;
	public static final int HOUSE_KEY = 4;
	
	/** Item representation of all of the items */
	public static final Item[] items = {
			null,
			WEAPON
			//new Item(2, Item.Type.GENERAL, "Armor", 50),
			//new Item(3, Item.Type.GENERAL, "Iron Ore", 50),
			//new Item(3, Item.Type.QUEST, "House Key", 0)
	};
	
	/** Item stack representation of all of the items */
	public static final ItemStack[] stacks = {
			null,
			new ItemStack(1, WEAPON),
			// new ItemStack(1, items[ARMOR]),
			// new ItemStack(32, items[IRON_ORE]),
			// new ItemStack(1, items[HOUSE_KEY])
	};
	
	/** */
	public static final String ground_assets[] = {
			null,
			"item.png",
			"item.png",
			"item.png",
			"item.png",
	};
	
	public static void loadAssets(AssetManager assets) throws IOException
	{
		for(Item i : items)
			if(i != null) i.loadImages(assets);
	}
}
