package com.upl.mmorpg.game.uuid;

import java.util.UUID;

public class CharacterUUID extends BaseUUID
{
	public CharacterUUID(UUID uuid) 
	{
		super(uuid);
	}
	
	public static CharacterUUID generate()
	{
		return new CharacterUUID(UUID.randomUUID());
	}

	private static final long serialVersionUID = 4641453441424526185L;
}
