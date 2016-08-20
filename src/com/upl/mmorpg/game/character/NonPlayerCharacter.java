package com.upl.mmorpg.game.character;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.lib.animation.WanderAnimation;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class NonPlayerCharacter extends MMOCharacter 
{
	public NonPlayerCharacter(double x, double y, double width, double height,
			Grid2DMap map, AssetManager assets, Game game, CharacterUUID entity_id) 
	{
		super(x, y, width, height, map, assets, game, entity_id);
	}

	@Override
	public abstract String getRenderName();

	public void wander(int radius)
	{
		WanderAnimation wander = new WanderAnimation(game, animation, this, map, -1);
		wander.setRadius(radius);
		animation.transitionTo(wander);
	}
	
	public void addWander(int radius, int duration)
	{
		WanderAnimation wander = new WanderAnimation(game, animation, this, map, duration);
		wander.setRadius(radius);
		animation.addAnimation(wander);
	}
	
	private static final long serialVersionUID = -1096329246846177789L;
}
