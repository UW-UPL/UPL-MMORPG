package com.upl.mmorpg.game.uuid;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents the basic functions of a UUID value. This is used as a base class
 * for the other types of UUIDs. We want to 
 * @author john
 *
 */

public abstract class BaseUUID implements Serializable
{
	public BaseUUID(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BaseUUID)
		{
			BaseUUID u = (BaseUUID)obj;
			return u.uuid.equals(this.uuid);
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return uuid.toString();
	}
	
	private UUID uuid; /**< The underlying UUID value */
	
	private static final long serialVersionUID = 3543652776975780500L;
}
