package com.upl.mmorpg.game.uuid;

import java.util.UUID;

public class ItemUUID extends BaseUUID 
{
	public ItemUUID(UUID uuid) 
	{
		super(uuid);
	}
	
	public static ItemUUID generate()
	{
		return new ItemUUID(UUID.randomUUID());
	}
	
	private static final long serialVersionUID = -1654434156633615869L;
}
