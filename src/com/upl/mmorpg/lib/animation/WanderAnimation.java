package com.upl.mmorpg.lib.animation;

import java.util.Random;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.map.MapSquare;

public class WanderAnimation extends Animation implements AnimationListener
{
	public WanderAnimation(Game game, AnimationManager manager, MMOCharacter character,
			Grid2DMap map, AnimationListener listener, int duration)
	{
		super(game, manager, character, listener, duration);

		this.map = map;
		isWalking = false;
		animating = false;
		this.startRow = character.getRow();
		this.startCol = character.getColumn();
		radius = 0;
		this.seed = System.nanoTime();
		random = new Random(seed);
	}

	@Override
	public void animationInterrupted(Animation source) 
	{
		//if(source == walking || source == idle) return;
		
		/* This was an external interrupt */
		//walking.animationInterrupted(source);
		//idle.animationInterrupted(source);
		isWalking = false;
		animating = false;
	}

	@Override
	public void animationStarted() 
	{
		if(radius == 0) return;
		isWalking = false;
		animating = true;
		wander();
	}
	
	public void setRadius(int radius)
	{
		this.radius = radius;
	}

	private void wander()
	{
		int attempts = 0;
		while(animating && !isWalking)
		{
			if(attempts == 100) break;
			attempts++;
			/* Generate a new row and col to walk to */
			int row = this.startRow + random.nextInt((radius * 2) + 1) - radius;
			int col = this.startCol + random.nextInt((radius * 2) + 1) - radius;

			MapSquare square = map.getSquare(row, col);
			if(square == null || !square.isPassable()) continue;
			
			isWalking = true;
			//GridGraph graph = new GridGraph(character.getRow(), 
			//		character.getColumn(), map);
			//Path p = graph.shortestPathTo(row, col);
			//walking.setPath(p);
			//manager.setAnimation(walking);
		}
	}

	@Override
	public void animationReelFinished() 
	{
		//if(isWalking)
		//	walking.animationReelFinished();
		//else idle.animationReelFinished();
	}

	@Override
	public void animation(double seconds) 
	{
		//if(isWalking)
		//	walking.animation(seconds);
		//else idle.animation(seconds);
	}

	@Override
	public void directionChanged(int direction) 
	{
		//if(isWalking)
		//	walking.directionChanged(direction);
		//else idle.directionChanged(direction);
	}

	@Override
	public void animationFinished(Animation animation) 
	{
		if(isWalking)
		{
			isWalking = false;
			//manager.setAnimation(idle);
			/* Idle for a bit and then wander more */
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					try 
					{
						Thread.sleep(1000 * (random.nextInt(5) + 1));
					} catch (InterruptedException e) {}
					wander();

				}
			};
			new Thread(run).start();
		}
	}

	public void updateTransient(Game game, MMOCharacter character)
	{
		//super.updateTransient(game, null, character);
		random = new Random(seed);
		map = character.getCurrentMap();
		
	//	walking.updateTransient(game, this, character);
		//idle.updateTransient(game, this, character);
	}
	
	private long seed;
	private transient Random random;
	private transient Grid2DMap map;
	private int radius;
	private int startRow;
	private int startCol;

	private boolean animating;
	private boolean isWalking;
	
	private static final long serialVersionUID = 9139723565757939753L;
}
