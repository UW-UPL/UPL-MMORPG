package com.upl.mmorpg.lib.animation.effect;

import java.awt.Graphics2D;
import java.io.Serializable;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.gui.AssetManager;

public abstract class CharacterEffect implements Serializable
{
	public CharacterEffect(MMOCharacter character, AssetManager assets)
	{
		this.character = character;
		this.assets = assets;
	}
	
	/**
	 * Draw the effect.
	 * @param g The graphics to use to draw the effect
	 */
	public abstract void render(Graphics2D g);
	
	/**
	 * Animate the character effect. Return true if the effect
	 * has finished and should be removed. Return false if the
	 * effect is not done and needs more rendering.
	 * @param seconds How many seconds have passed since the 
	 * 			last render.
	 * @return Whether or not rendering is complete.
	 */
	public abstract boolean animation(double seconds);
	
	protected AssetManager assets; /** Asset manager for this effect */
	protected MMOCharacter character; /** The character who owns this effect. */
	
	private static final long serialVersionUID = -5793681997575957647L;
}
