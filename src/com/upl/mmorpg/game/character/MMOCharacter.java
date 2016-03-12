package com.upl.mmorpg.game.character;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.animation.AnimationManager;
import com.upl.mmorpg.lib.animation.FollowAnimation;
import com.upl.mmorpg.lib.animation.IdleAnimation;
import com.upl.mmorpg.lib.animation.WalkingAnimation;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.map.Grid2DMap;

public abstract class MMOCharacter extends Renderable
{
	public MMOCharacter(int row, int col,
			Grid2DMap map, AssetManager assets)
	{
		this(col * map.getTileSize(), row * map.getTileSize(),
				map.getTileSize(), map.getTileSize(),
				map, assets);
	}
	
	public MMOCharacter(double x, double y, double width, double height, 
			Grid2DMap map, AssetManager assets)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.map = map;
		this.assets = assets;
		
		/* default values for character properties */
		walkingSpeed = 1.0d;
		
		hasAnimation = true;
		
		animation = new AnimationManager(assets);
		walking = new WalkingAnimation(animation, this, map.getTileSize(), null);
		idle = new IdleAnimation(animation, this, map.getTileSize(), null);
		follow = new FollowAnimation(animation, this, map, 
				map.getTileSize(), null);
		followers = new LinkedList<FollowListener>();
	}
	
	public void follow(MMOCharacter character)
	{
		follow.setFollee(character);
		animation.setAnimation(follow);
	}
	
	public void addFollower(FollowListener follow)
	{
		followers.add(follow);
	}
	
	public void removeFollower(FollowListener follow)
	{
		followers.remove(follow);
	}
	
	public Iterator<FollowListener> getFollowers()
	{
		return followers.iterator();
	}
	
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
	
	public void idle()
	{
		animation.setAnimation(idle);
		animation.setAnimationSpeed(10);
	}
	
	@Override public abstract String getRenderName();
	
	protected AssetManager assets;
	protected Grid2DMap map;
	protected AnimationManager animation;
	
	protected IdleAnimation idle;
	protected WalkingAnimation walking;
	protected FollowAnimation follow;
	
	protected LinkedList<FollowListener> followers;

	public double getWalkingSpeed() { return walkingSpeed; }
	public void setWalkingSpeed(double speed) { this.walkingSpeed = speed; }
	public int getRow(){return (int)(locY / map.getTileSize());}
	public int getCol(){return (int)(locX / map.getTileSize());}
	
	/** Character properties (time related) */
	protected double walkingSpeed; /* Horizontal/Vertical tiles per second */
}
