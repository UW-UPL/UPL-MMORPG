package com.upl.mmorpg.game.item;

import com.upl.mmorpg.lib.util.StackBuffer;
import com.upl.mmorpg.lib.util.StackBufferable;

public class Item implements StackBufferable
{
	protected Item() {}
	public Item(Item i)
	{
		id = i.id;
		type = i.type;
		name = i.name;
		value = i.value;
	}
	
	public Item(int id, Item.Type type, String name, long value)
	{
		this.id = id;
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public Item(StackBuffer buff)
	{
		popFromStackBuffer(buff);
	}
	
	@Override
	public StackBuffer pushToStackBuffer(StackBuffer buff) 
	{
		buff.pushInt(id);
		buff.pushString("" + type);
		buff.pushString(name);
		buff.pushLong(value);
		
		return buff;
	}

	@Override
	public StackBuffer popFromStackBuffer(StackBuffer buff) 
	{
		id = buff.popInt();
		type = Item.Type.valueOf(buff.popString());
		name = buff.popString();
		value = buff.popLong();
		
		return buff;
	}
	
	public int getId() {return id;}
	public Item.Type getType() {return type;}
	public String getName() {return name;}
	public long getValue() {return value;}

	private int id; /**< The ID of the item */
	private Item.Type type; /**< The type of the object */
	private String name; /**< The name of the item. */
	private long value; /**< The value of the item. */
	
	public enum Type
	{
		NONE, GENERAL, QUEST
	}
}
