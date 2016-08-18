package com.upl.mmorpg.lib.quest;

import java.io.Serializable;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.item.Item;

public interface Quest extends Serializable
{
	public void died(MMOCharacter character);
	public void killed(MMOCharacter attacker, MMOCharacter victim);
	public void damaged(MMOCharacter attacker, MMOCharacter victim, int damage);
	public void movedTo(MMOCharacter character, int row, int col);
	public void pickedUpItem(MMOCharacter character, Item i);
	public void droppedItem(MMOCharacter character, Item i);
}
