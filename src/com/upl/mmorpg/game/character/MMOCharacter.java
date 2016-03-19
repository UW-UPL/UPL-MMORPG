package com.upl.mmorpg.game.character;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.GridPoint;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.animation.AnimationManager;
import com.upl.mmorpg.lib.animation.DeathAnimation;
import com.upl.mmorpg.lib.animation.FollowAnimation;
import com.upl.mmorpg.lib.animation.IdleAnimation;
import com.upl.mmorpg.lib.animation.PunchAnimation;
import com.upl.mmorpg.lib.animation.WalkingAnimation;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class MMOCharacter extends Renderable
{
	public MMOCharacter(int row, int col,
			Grid2DMap map, AssetManager assets, Game game)
	{
		this(col * map.getTileSize(), row * map.getTileSize(),
				map.getTileSize(), map.getTileSize(),
				map, assets, game);
	}
	
	public MMOCharacter(double x, double y, double width, double height, 
			Grid2DMap map, AssetManager assets, Game game)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.map = map;
		this.assets = assets;
		this.health = 0;
		this.attackSpeed = 0.0d;
		this.game = game;
		
		/* default values for character properties */
		walkingSpeed = 1.0d;
		
		hasAnimation = true;
		
		animation = new AnimationManager(assets);
		walking = new WalkingAnimation(animation, this, map.getTileSize(), null);
		idle = new IdleAnimation(animation, this, map.getTileSize(), null);
		attack = new PunchAnimation(animation, this, map.getTileSize(), null);
		death = new DeathAnimation(animation, this, map.getTileSize(), null, game);
		follow = new FollowAnimation(animation, this, map, 
				map.getTileSize(), null);
		followers = new LinkedList<FollowListener>();
	}
	
	public void follow(MMOCharacter character)
	{
		follow.setFollee(character);
		animation.setAnimation(follow);
	}
	public void addFollower(FollowListener follow) { followers.add(follow); }
	public void removeFollower(FollowListener follow) { followers.remove(follow); }
	public Iterator<FollowListener> getFollowers() { return followers.iterator(); }
	public void idle() { animation.setAnimation(idle); }
	public void die() { animation.setAnimation(death); }
	public void attack(MMOCharacter character) { animation.setAnimation(attack); }
	
	@Override
	public void animation(double seconds)
	{
		animation.animation(seconds);
	}
	
	@Override
	public void render(Graphics2D g)
	{
		BufferedImage img = animation.getFrame();
		if(img == null) return;
		
		g.drawImage(animation.getFrame(), 
				(int)locX, (int)locY, (int)width, (int)height, null);
	}
	
	protected void setAnimationReels(String path) throws IOException
	{
		animation.loadReels(path);
	}
	
	public void background_walkTo(final int row, final int col)
	{
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				int startRow = getRow();
				int startCol = getCol();
				
				GridGraph graph = new GridGraph(startRow, startCol, map);
				Path p = graph.shortestPathTo(row, col);
				System.out.println("startRow: " + startRow + "  startCol: " + startCol);
				
				walking.setPath(p);
				animation.setAnimation(walking);
			}
		};
		new Thread(run).start();
	}

	public void walkTo(int row, int col)
	{
		int startRow = (int)(locY / map.getTileSize());
		int startCol = (int)(locX / map.getTileSize());
		
		GridGraph graph = new GridGraph(startRow, startCol, map);
		Path p = graph.shortestPathTo(row, col);
		
		walking.setPath(p);
		animation.setAnimation(walking);
	}
	
	public GridPoint getBehindPoint()
	{
		int row = getRow();
		int col = getCol();
		GridPoint point = null;
		
		switch(animation.getReelDirection())
		{
			case AnimationManager.FRONT:
				point = new GridPoint(row - 1, col);
				break;
			case AnimationManager.BACK:
				point = new GridPoint(row + 1, col);
				break;
			case AnimationManager.RIGHT:
				point = new GridPoint(row, col - 1);
				break;
			case AnimationManager.LEFT:
				point = new GridPoint(row, col + 1);
				break;
			case AnimationManager.FRONT_LEFT:
				point = new GridPoint(row - 1, col + 1);
				break;
			case AnimationManager.FRONT_RIGHT:
				point = new GridPoint(row - 1, col - 1);
				break;
			case AnimationManager.BACK_RIGHT:
				point = new GridPoint(row + 1, col - 1);
				break;
			case AnimationManager.BACK_LEFT:
				point = new GridPoint(row + 1, col + 1);
				break;
		}
		
		return point;
	}
	
	public Path getPath()
	{
		return walking.getPath();
	}
	
	public int getRow(){return (int)(locY / map.getTileSize());}
	public int getCol(){return (int)(locX / map.getTileSize());}
	@Override public abstract String getRenderName();
	
	protected AssetManager assets;
	protected Grid2DMap map;
	protected AnimationManager animation;
	protected Game game;
	
	/* Generic animations */
	protected IdleAnimation idle;
	protected WalkingAnimation walking;
	protected FollowAnimation follow;
	protected PunchAnimation attack;
	protected DeathAnimation death;
	
	/* Characters that are following this character */
	protected LinkedList<FollowListener> followers;

	/** Character properties (time related) */
	protected double walkingSpeed; /* Horizontal/Vertical tiles per second */
	
	protected int maxHealth; /* How much health the player can hold */
	protected int health; /* How much health the player has */
	protected double attackSpeed; /* How many attacks/second can this character do? */
	
	/** Getters/setters for properties */
	public double getWalkingSpeed() { return walkingSpeed; }
	public void setWalkingSpeed(double speed) { this.walkingSpeed = speed; }
	public int getMaxHealth() { return maxHealth; }
	public void setMaxHealth(int health) { this.maxHealth = health; }
	public int getHealth() { return health; }
	public void setHealth(int health) { this.health = health; }
	public double getAttackSpeed() { return attackSpeed; }
	public void setAttackSpeed(double attackSpeed) { this.attackSpeed = attackSpeed; }
}
