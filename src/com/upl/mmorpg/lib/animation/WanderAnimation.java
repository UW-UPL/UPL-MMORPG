package com.upl.mmorpg.lib.animation;

import java.util.Random;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.map.MapSquare;

public class WanderAnimation extends ComplexAnimation
{
	public WanderAnimation(Game game, AnimationManager manager, MMOCharacter character)
	{
		super(game, manager, character);

		this.map = character.getCurrentMap();
		this.startRow = character.getRow();
		this.startCol = character.getColumn();
		isWalking = false;
		radius = 0;
		random = new Random(System.nanoTime());
	}
	
	@Override
	public void animationStarted()
	{
		super.animationStarted();
		idle();
	}

	public void setRadius(int radius)
	{
		this.radius = radius;
	}

	private void wander()
	{
		int attempts = 0;
		while(attempts < 100)
		{
			attempts++;
			/* Generate a new row and col to walk to */
			int row = this.startRow + random.nextInt((radius * 2) + 1) - radius;
			int col = this.startCol + random.nextInt((radius * 2) + 1) - radius;

			MapSquare square = map.getSquare(row, col);
			if(square == null || !square.isPassable()) continue;
			
			Log.vln("Wander will now walk to (" + row + ", " + col + ")");
			WalkingAnimation walk = new WalkingAnimation(game, manager, character, row, col);
			
			/* Can we walk here? */
			if(!walk.calculatePath(map))
				continue;
			
			isWalking = true;
			manager.transitionTo(walk);
			return;
		}
		
		Log.e("WANDER FAILED");
		idle();
	}
	
	public void idle()
	{
		int duration = 2 + random.nextInt(3); /* amount of time to idle */
		duration *= 1000; /* convert to millis */
		isWalking = false;
		Log.vln("Wander animation will idle for " + duration + " milliseconds.");
		manager.transitionTo(new IdleAnimation(game, manager, character, duration));
	}

	@Override
	public void nextAnimation() 
	{
		if(!subManager.isPlayingDefault())
		{
			Log.vln("We are not playing the default animation, so we will pass for now.");
			return;
		}
		if(isWalking)
			idle();
		else wander();
	}
	
	@Override
	public void updateTransient(Game game, MMOCharacter character, AnimationManager manager)
	{
		super.updateTransient(game, character, manager);
		map = character.getCurrentMap();
	}
	
	@Override
	public String toString()
	{
		return "Wander Animation";
	}
	
	private transient Grid2DMap map; /**< The map we're currently on */
	
	private Random random; /**< Random used for generating wandering */
	private int radius; /**< The radius of our wander field */
	private int startRow; /**< The row we started from */
	private int startCol; /**< The column we started from */
	private boolean isWalking; /**< Whether or not we are walking right now */
	
	private static final long serialVersionUID = 9139723565757939753L;
}
