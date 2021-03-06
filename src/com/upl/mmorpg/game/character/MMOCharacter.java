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
import com.upl.mmorpg.game.uuid.CharacterUUID;
import com.upl.mmorpg.lib.algo.GridGraph;
import com.upl.mmorpg.lib.algo.GridPoint;
import com.upl.mmorpg.lib.algo.Path;
import com.upl.mmorpg.lib.animation.Animation;
import com.upl.mmorpg.lib.animation.AnimationManager;
import com.upl.mmorpg.lib.animation.DeathAnimation;
import com.upl.mmorpg.lib.animation.DropItemAnimation;
import com.upl.mmorpg.lib.animation.IdleAnimation;
import com.upl.mmorpg.lib.animation.PickupItemAnimation;
import com.upl.mmorpg.lib.animation.WalkingAnimation;
import com.upl.mmorpg.lib.animation.effect.CharacterEffect;
import com.upl.mmorpg.lib.animation.effect.DamageEffect;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.Renderable;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.Grid2DMap;
import com.upl.mmorpg.lib.quest.Quest;

/**
 * Represents the base class for all characters in the game. This class
 * should be extended by all regular and non player characters. This class
 * should be able to be serialized with little or no updating required on
 * the client side. All animations also need to be serializable.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */
public abstract class MMOCharacter extends Renderable implements Serializable
{
	protected MMOCharacter(double x, double y, double width, double height, 
			Grid2DMap map, Game game, CharacterUUID uuid)
	{
		super();
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.map = map;
		this.assets = game.getAssetManager();
		this.health = 0;
		this.attackSpeed = 0.0d;
		this.game = game;
		this.currentQuest = null;
		this.quests = new LinkedList<Quest>();
		this.uuid = uuid;
		hasAnimation = true;
		
		animation = new AnimationManager(game, this);
		followers = new LinkedList<CharacterUUID>();
		effects = new LinkedList<CharacterEffect>();
		inventory = new Inventory();
		
		/* default values for character properties */
		walkingSpeed = 1.0d;
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
	
	/**
	 * Walk to the given row and column.
	 * @param row The row to go to.
	 * @param col The column to go to.
	 */
	public void walkTo(int row, int col)
	{
		WalkingAnimation walking = new WalkingAnimation(game, animation, this, row, col);
		animation.transitionTo(walking);
	}
	
	/**
	 * Walk along a path. If the path isn't right next to the character,
	 * it will be navigated to first.
	 * @param p The path to walk upon.
	 */
	public void walkAlong(Path p)
	{
		Log.vln(this + " is walking along a path!");
		WalkingAnimation walking = new WalkingAnimation(game, animation, this, 0, 0);
		walking.alongPath(p, getCurrentMap());
		animation.transitionTo(walking);
	}
	
	/**
	 * Walk along a path. If the path isn't right next to the character,
	 * it will be navigated to first.
	 * @param p The path to walk upon.
	 */
	public void addWalkAlong(Path p)
	{
		Log.vln(this + " is walking along a path!");
		WalkingAnimation walking = new WalkingAnimation(game, animation, this, 0, 0);
		walking.alongPath(p, getCurrentMap());
		animation.addAnimation(walking);
	}
	
	/**
	 * Add a walking animation to the queue. This can safely be added
	 * from any thread.
	 * @param row The row to walk to.
	 * @param col The column to walk to.
	 */
	public void addWalkTo(int row, int col)
	{
		Log.vln("Adding walk to.");
		WalkingAnimation walking = new WalkingAnimation(game, animation, this, row, col);
		animation.addAnimation(walking);
	}
	
	/**
	 * Follow the given character.
	 * @param character The character to follow.
	 */
	public void follow(MMOCharacter character)
	{
		/* Move to that character */
		character.addFollower(uuid);
		GridGraph graph = new GridGraph(getRow(), getColumn(), character.getCurrentMap());
		Path walkingPath = graph.shortestPathTo(character.getRow(), character.getColumn());
		walkingPath.removeLastPoint();
		if(walkingPath.isEmpty())
			return;
		walkTo(walkingPath.getLast().getRow(), walkingPath.getLast().getColumn());
	}
	
	/**
	 * Add a follow animation to the animation queue.
	 * @param character The character to follow.
	 */
	public void addFollow(MMOCharacter character, int duration)
	{
		//FollowAnimation follow = new FollowAnimation(game, animation, this, map, duration);
		//follow.setFollee(character);
		//animation.addAnimation(follow);
	}
	
	public void addFollower(CharacterUUID follow) { followers.add(follow); }
	public void removeFollower(CharacterUUID follow) { followers.remove(follow); }
	public Iterator<CharacterUUID> getFollowers() { return followers.iterator(); }
	
	/**
	 * Add an idle animation to the queue for the given duration. A duration of -1 specifies
	 * to idle forever.
	 * @param duration The duration to idle for, in milliseconds.
	 */
	public void addIdle(int duration)
	{
		IdleAnimation idle = new IdleAnimation(game, animation, this, duration);
		animation.addAnimation(idle); 
	}
	
	/**
	 * Idle this character forever (until next animation is sets).
	 */
	public void idle() 
	{ 
		IdleAnimation idle = new IdleAnimation(game, animation, this, -1);
		animation.transitionTo(idle); 
	}
	
	/**
	 * Idle this character for the given amount of milliseconds.
	 */
	public void idle(int duration) 
	{ 
		IdleAnimation idle = new IdleAnimation(game, animation, this, duration);
		animation.transitionTo(idle); 
	}
	
	/**
	 * Add a death animation to the animation queue.
	 */
	public void addDie()
	{
		DeathAnimation death = new DeathAnimation(game, animation, this);
		animation.addAnimation(death); 
	}
	
	/**
	 * Kill the character immediately
	 */
	public void die() 
	{ 
		DeathAnimation death = new DeathAnimation(game, animation, this);
		animation.transitionTo(death); 
	}
	
	/**
	 * Animate the current character attacking the given character.
	 * @param character The character to attack.
	 */
	public void attack(MMOCharacter character) 
	{
		//PunchAnimation attack = new PunchAnimation(game, animation, this, map);
		//attack.setAttacking(character);
		//animation.transitionTo(attack); 
	}
	
	/**
	 * Pick up an item on the map immediately.
	 * @param row The row the item is in.
	 * @param col The column the item is in.
	 * @param item The item to pick up.
	 */
	public void pickupItem(int row, int col, Item item)
	{
		PickupItemAnimation item_animation = new PickupItemAnimation(game, animation, this, item);
		if(getRow() != row || getColumn() != col)
		{
			walkTo(row, col);
			animation.addAnimation(item_animation);
		} else animation.transitionTo(item_animation);
	}
	
	/**
	 * Add a pickup item animation to the animation queue.
	 * @param row The row the item is in.
	 * @param col The column the item is in.
	 * @param item The item to pick up.
	 */
	public void addPickupItem(int row, int col, Item item)
	{
		PickupItemAnimation item_animation = new PickupItemAnimation(game, animation, this, item);
		if(getRow() != row || getColumn() != col)
		{
			addWalkTo(row, col);
			animation.addAnimation(item_animation);
		} else animation.addAnimation(item_animation);
	}
	
	/**
	 * Animate the character dropping the item on the map.
	 * @param row The row on the map to drop the item.
	 * @param col The column on the map to drop the item.
	 * @param item The item to drop on the map.
	 */
	public void dropItem(int row, int col, Item item)
	{
		DropItemAnimation drop_animation = new DropItemAnimation(game, animation, this, item);
		if(getRow() != row || getColumn() != col)
		{
			walkTo(row, col);
			animation.addAnimation(drop_animation);
		} else animation.transitionTo(drop_animation);
	}
	
	/**
	 * Animate the character dropping the item on the map.
	 * @param row The row on the map to drop the item.
	 * @param col The column on the map to drop the item.
	 * @param item The item to drop on the map.
	 */
	public void addDropItem(int row, int col, Item item)
	{
		DropItemAnimation drop_animation = new DropItemAnimation(game, animation, this, item);
		if(getRow() != row || getColumn() != col)
		{
			addWalkTo(row, col);
			animation.addAnimation(drop_animation);
		} else animation.addAnimation(drop_animation);
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
		if(img == null) 
		{
			Log.e("MMOCharacter frame is null!");
			return;
		}
		
		drawImage(panel, g, img, locX, locY, width, height);
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
	public void animationFinished(Animation animation){}
	
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
	public void setCurrentMap(Grid2DMap map) { this.map = map;}
	public int getCurrentMapID() { return map.getID(); }
	public int getRow() { return (int)(locY + 0.5d); }
	public int getColumn() { return (int)(locX + 0.5d); }
	public CharacterUUID getUUID() { return uuid; }
	public Inventory getInventory() { return inventory; }
	
	/**
	 * Place the character in a specific row on the map. The
	 * column on the map remains unchanged.
	 * @param row The row in which to place the character.
	 */
	public void setRow(int row) 
	{
		this.locY = (double)row + 0.5d;
	}
	
	/**
	 * Place the character in a specific character. The row
	 * remains unchanged.
	 * @param col The column in which to place the character.
	 */
	public void setColumn(int col) 
	{ 
		this.locX = (double)col + 0.5d;
	}
	
	/**
	 * Put the character into the given row and column on the map.
	 * @param row The row to place the character.
	 * @param col The column to place the character.
	 */
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
			// attacker.interruptAttack();
		}
		
		return amount;
	}
	
	/**
	 * Add the given item to the character's inventory. Returns
	 * whether or not there was room in the inventory to place
	 * the item.
	 * @param i The item to give to the player.
	 * @return Whether or not the item could be placed into the character's inventory.
	 */
	public boolean receiveItem(Item i)
	{
		return inventory.add(i);
	}
	
	/**
	 * Take the item out of the player's inventory.
	 * @param i The item to take away from the player.
	 * @return Whether or not the item could be taken out of the player's inventory.
	 */
	public boolean dropItem(Item i)
	{
		return inventory.remove(i);
	}
	
	/**
	 * Update any transient elements that didn't make it across the network.
	 * @param assets The asset manager to use for loading assets.
	 * @param game The game that we are playing in.
	 * @param map The map that this character is on.
	 * @throws IOException Any exceptions raised from reading in assets.
	 */
	public void updateTransient(AssetManager assets, Game game, Grid2DMap map) throws IOException
	{
		this.assets = assets;
		this.game = game;
		this.map = map;
		animation.updateTransient(game, this);
		
		effects = new LinkedList<CharacterEffect>();
		quests = new LinkedList<Quest>();
		currentQuest = null;
	}
	
	/**
	 * Update the current character with the properties of the given character.
	 * @param character The new character properties.
	 * @throws IOException If there was an exception raised during asset load time.
	 */
	public void update(MMOCharacter character) throws IOException
	{
		/* Temporarily disable rendering */
		renderable = false;
		hasAnimation = false;
		
		/* Update animations */
		this.animation = character.animation;
		animation.updateTransient(game, this);
		
		/* Update properties */
		this.walkingSpeed = character.walkingSpeed;
		this.animation = character.animation;
		this.maxHealth = character.maxHealth;
		this.attackSpeed = character.attackSpeed;
		this.inventory = character.inventory;
		
		/* reenable rendering */
		renderable = true;
		hasAnimation = true;
	}
	
	/**
	 * Notify all of the followers that we are now walking.
	 * @param p The path we are planning on following.
	 */
	public void notifyFolowers(Path p)
	{	
		p.addFirstPoint(getRow(), getColumn());
		p = p.copy();
		p.removeLastPoint();
		if(p.isEmpty())
			return;
		
		for(CharacterUUID id : followers)
		{
			MMOCharacter character = game.getCharacter(id);
			if(character == null)
			{
				Log.e("I don't know where character " + id + " is. (notifyFollowers)");
				continue;
			}
			
			Log.vln("Character " + character + " notified by character they are following.");
			character.idle(250);
			character.addWalkAlong(p);
			game.characterUpdated(character, false);
		}
	}
	
	protected transient AssetManager assets; /**< The asset manager to use for loading assets. */
	protected transient Grid2DMap map; /**< The map the character is currently playing on. */
	protected transient Game game; /**< The game the player is currently playing in. */
	
	protected transient Quest currentQuest;
	protected transient LinkedList<Quest> quests;
	protected transient LinkedList<CharacterEffect> effects;
	
	/* Generic animations */
	protected AnimationManager animation;
	
	/* Characters that are following this character */
	protected LinkedList<CharacterUUID> followers; /**< The characters who are following us */

	/** Character properties (time related) */
	protected CharacterUUID uuid; /**< The unique identity number for this character  */
	protected String name; /**< The character's name */
	
	protected double walkingSpeed; /**< Horizontal/Vertical tiles per second */
	protected int maxHealth; /**< How much health the player can hold */
	protected int health; /**< How much health the player has */
	protected double attackSpeed; /**< How many attacks/second can this character do? */
	protected Inventory inventory; /**< The character's inventory */
	
	private static final long serialVersionUID = 8281796539996826293L;
}
