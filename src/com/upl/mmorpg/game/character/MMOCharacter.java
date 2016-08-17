package com.upl.mmorpg.game.character;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.item.Inventory;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.GridPoint;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.animation.AnimationManager;
import com.upl.mmorpg.lib.animation.AttackAnimation;
import com.upl.mmorpg.lib.animation.DeathAnimation;
import com.upl.mmorpg.lib.animation.FollowAnimation;
import com.upl.mmorpg.lib.animation.IdleAnimation;
import com.upl.mmorpg.lib.animation.PunchAnimation;
import com.upl.mmorpg.lib.animation.WalkingAnimation;
import com.upl.mmorpg.lib.animation.effect.CharacterEffect;
import com.upl.mmorpg.lib.animation.effect.DamageEffect;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.quest.Quest;

public abstract class MMOCharacter extends Renderable implements Serializable
{
	protected MMOCharacter(double x, double y, double width, double height, 
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
		this.currentQuest = null;
		this.quests = new LinkedList<Quest>();
		
		/* default values for character properties */
		walkingSpeed = 1.0d;
		
		hasAnimation = true;
		
		animation = new AnimationManager(assets);
		attack = null;
		walking = new WalkingAnimation(game, animation, this, null);
		idle = new IdleAnimation(game, animation, this, null);
		death = new DeathAnimation(game, animation, this, null);
		follow = new FollowAnimation(game, animation, this, map, null);
		followers = new LinkedList<FollowListener>();
		effects = new LinkedList<CharacterEffect>();
		inventory = new Inventory();
	}
	
	/**
	 * Set the global position of the character.
	 * @param x The x position of the character.
	 * @param y The y position of the character.
	 */
	public void setPosition(double x, double y)
	{
		this.locX = x;
		this.locY = y;
	}
	
	/**
	 * Set the position of this character based on a row and
	 * column.
	 * @param row The row of the position of the character.
	 * @param column The column of the position of the character.
	 */
	public void setGridPosition(int row, int column)
	{
		this.locX = column;
		this.locY = row;
	}
	
	/** Animation methods */
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
	public void attack(MMOCharacter character) 
	{
		attack = new PunchAnimation(game, animation, this, map, null);
		attack.setAttacking(character);
		animation.setAnimation(attack); 
	}
	public void interruptAttack()
	{
		attack.interruptAttack();
	}
	
	@Override
	public void animation(double seconds)
	{
		animation.animation(seconds);
		
		/* Animate any particles or other effects */
		Iterator<CharacterEffect> it = effects.iterator();
		while(it.hasNext())
			if(it.next().animation(seconds))
				it.remove();
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel)
	{
		BufferedImage img = animation.getFrame();
		if(img == null) return;
		
		drawImage(panel, g, animation.getFrame(), locX, locY, width, height);
	}
	
	@Override
	public void renderEffects(Graphics2D g)
	{
		/* Draw any particles or other effects */
		Iterator<CharacterEffect> it = effects.iterator();
		while(it.hasNext())
			it.next().render(g);
	}
	
	/**
	 * Set the animation reels for the given character.
	 * @param path The path to the animation reels directory.
	 * @throws IOException The reels couldn't be loaded
	 */
	protected void setAnimationReels(String path) throws IOException
	{
		animation.loadReels(path);
	}
	
	/**
	 * Walk to the given row and column. Safe to call from a rendering or
	 * the main thread.
	 * @param row The row to go to.
	 * @param col The column to go to.
	 */
	public void background_walkTo(final int row, final int col)
	{
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				int startRow = getRow();
				int startCol = getColumn();
				
				GridGraph graph = new GridGraph(startRow, startCol, map);
				Path p = graph.shortestPathTo(row, col);
				System.out.println("startRow: " + startRow + "  startCol: " + startCol);
				
				walking.setPath(p);
				animation.setAnimation(walking);
			}
		};
		new Thread(run).start();
	}

	/**
	 * Walk to the given row and column. DO NOT call this from a rendering
	 * thread. Instead call background_walkTo, which uses a seperate thread
	 * to do the shortest path computation.
	 * @param row The row to go to.
	 * @param col The column to go to.
	 */
	public void walkTo(int row, int col)
	{
		int startRow = getRow();
		int startCol = getColumn();
		
		GridGraph graph = new GridGraph(startRow, startCol, map);
		Path p = graph.shortestPathTo(row, col);
		
		walking.setPath(p);
		animation.setAnimation(walking);
	}
	
	/**
	 * Get the grid point behind the character.
	 * @return The grid point that is directly behind the character.
	 */
	public GridPoint getBehindPoint()
	{
		int row = getRow();
		int col = getColumn();
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
	
	/**
	 * Get the current path if this character is currently
	 * walking to a destination.
	 * @return The path of this character.
	 */
	public Path getPath()
	{
		return walking.getPath();
	}
	
	/** Quest methods */
	
	/**
	 * Set the current quest for this character. Return true if the character
	 * is on this quest, false otherwise.
	 * @param quest The quest to assign.
	 * @return Whether or not the character can make this quest active.
	 */
	public boolean setCurrentQuest(Quest quest)
	{
		if(quests.contains(quest))
		{
			currentQuest = quest;
			return true;
		}
		return false;
	}
	public Quest getCurrentQuest() { return currentQuest; }
	public void startQuest(Quest quest) { this.quests.add(quest); }
	public void questComplete(Quest quest) { this.quests.remove(quest); }
	public Iterator<Quest> getQuestIterator() { return quests.iterator(); }
	@Override public abstract String getRenderName();
	
	/** Getters/setters for properties */
	public void addEffect(CharacterEffect effect) { effects.add(effect); }
	public void removeEffect(CharacterEffect effect) { effects.remove(effect); }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public double getWalkingSpeed() { return walkingSpeed; }
	public void setWalkingSpeed(double speed) { this.walkingSpeed = speed; }
	public int getMaxHealth() { return maxHealth; }
	public void setMaxHealth(int health) { this.maxHealth = health; }
	public int getHealth() { return health; }
	public void setHealth(int health) { this.health = health; }
	public double getAttackSpeed() { return attackSpeed; }
	public void setAttackSpeed(double attackSpeed) { this.attackSpeed = attackSpeed; }
	public Grid2DMap getCurrentMap() { return map; }
	public int getCurrentMapID() { return map.getID(); }
	public int getRow() { return (int)(locY + 0.5d); }
	public int getColumn() { return (int)(locX + 0.5d); }
	
	public void setRow(int row) 
	{
		this.locY = (double)row + 0.5d;
	}
	
	public void setColumn(int col) 
	{ 
		this.locX = (double)col + 0.5d;
	}
	
	public void setPosition(int row, int col)
	{
		this.locX = (double)col + 0.5d;
		this.locY = (double)row + 0.5d;
	}
	
	/**
	 * Give the character some damage. This may kill the character
	 * if their health is not enough to sustain the damage. If the
	 * character doesn't have enough health to take the damage, the
	 * maximum amount of damage is returned.
	 * 
	 * @param amount The amount of damage to deal.
	 * @return The actual amount of damage taken
	 */
	public int takeDamage(int amount, MMOCharacter attacker)
	{
		/* Add a splat to our character */
		DamageEffect effect = new DamageEffect(this, assets);
		if(amount > health)
			amount = health;
		health -= amount;
		
		/* Set the displayed amount on the effect */
		effect.setAmount(amount);
		
		try 
		{
			/* Load all of the assets for this effect */
			effect.loadAssets();
			/* Add the effect to this character*/
			this.addEffect(effect);
		} catch (IOException e) 
		{
			Log.wtf("Couldn't load damage effect image!", e);
			return -1;
		}
		
		if(health <= 0)
		{
			this.die();
			attacker.interruptAttack();
		}
		
		return amount;
	}
	
	public boolean receiveItem(Item i)
	{
		return inventory.addItem(i);
	}
	
	protected transient AssetManager assets;
	protected transient Grid2DMap map;
	protected transient AnimationManager animation;
	protected transient Game game;
	protected transient Quest currentQuest;
	protected transient LinkedList<Quest> quests;
	protected transient LinkedList<CharacterEffect> effects;
	
	/* Generic animations */
	protected transient IdleAnimation idle;
	protected transient WalkingAnimation walking;
	protected transient FollowAnimation follow;
	protected transient AttackAnimation attack;
	protected transient DeathAnimation death;
	
	/* Characters that are following this character */
	protected transient LinkedList<FollowListener> followers;

	/** Character properties (time related) */
	protected String name; /**< The character's name */
	protected double walkingSpeed; /**< Horizontal/Vertical tiles per second */
	
	protected int maxHealth; /**< How much health the player can hold */
	protected int health; /**< How much health the player has */
	protected double attackSpeed; /**< How many attacks/second can this character do? */
	protected Inventory inventory; /**< The character's inventory */
	
	private static final long serialVersionUID = 8281796539996826293L;
}
