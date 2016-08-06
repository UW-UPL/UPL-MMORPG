package com.upl.mmorpg.game.item;

public class Item 
{
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
	
	public int getId() {return id;}
	public Item.Type getType() {return type;}
	public String getName() {return name;}
	public long getValue() {return value;}

	private final int id; /**< The ID of the item */
	private final Item.Type type; /**< The type of the object */
	private final String name; /**< The name of the item. */
	private final long value; /**< The value of the item. */
	
	public enum Type
	{
		NONE, GENERAL, QUEST
	}
}
