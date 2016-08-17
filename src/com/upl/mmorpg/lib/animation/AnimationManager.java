package com.upl.mmorpg.lib.animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderMath;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class AnimationManager implements Serializable
{
	public AnimationManager(AssetManager assets, MMOCharacter character)
	{
		this.assets = assets;
		this.character = character;
		currentReel = null;
		reelPos = -1;
		reelDirection = FRONT;
		currentFrame = null;
		animation_total = 0;
		animation_speed = 0;
		endReelNotified = false;
		currentReelName = null;
		this.reelsPath = null;
		animationQueue = new LinkedList<Animation>();
		
		map = new HashMap<String, BufferedImage[][]>();
	}
	
	public boolean setReel(String reelName, boolean loop)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return false;
		
		if(this.currentReel == reel) return true;
		this.currentReel = reel;
		this.reelPos = 0;
		this.animation_total = 0;
		this.animation_loop = loop;
		this.maxReelPos = reel[this.reelDirection].length;
		this.endReelNotified = false;
		this.currentReelName = reelName;
		
		this.currentFrame = reel[this.reelDirection][0];
		
		return true;
	}
	
	private boolean loadReel(String reelName)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return false;
		
		currentReel = reel;
		maxReelPos = reel[reelDirection].length;
		endReelNotified = false;
		currentReelName = reelName;
		
		currentFrame = reel[reelDirection][reelPos];
		
		return true;
	}
	
	public int getMaxFrameForReel(String reelName)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return -1;
		return reel[0].length;
	}

	public synchronized void addAnimation(Animation animation)
	{
		if(animationQueue.isEmpty())
			setAnimation(animation);
		else animationQueue.add(animation);
		Log.vln("Animation " + animation + " added to animation manager");
	}
	
	public void clearAnimations()
	{
		if(animationQueue.isEmpty())
			return;
		
		/* notify the current animation that they're being interrupted */
		Animation currentAnimation = animationQueue.getFirst();
		currentAnimation.animationInterrupted(null);
		animationQueue.clear();
		endReelNotified = false;
		setAnimationFrame(0);
		Log.vln("Cleared animations in animation manager.");
	}
	
	public boolean nextAnimation()
	{
		if(animationQueue.isEmpty())
			return false;
		
		Animation animation = animationQueue.removeFirst();
		animation.animationInterrupted(null);
		if(!animationQueue.isEmpty())
		{
			Animation next = animationQueue.getFirst();
			next.animationStarted();
		} else {
			/* The default animation is to idle */
			character.idle();
		}
		
		return true;
	}
	
	public void setAnimation(Animation animation)
	{
		Log.vln("New animation set: " + animation);
		animationQueue.clear();
		animationQueue.add(animation);
		animation.animationStarted();
		Log.vln("animationStarted called.");
		endReelNotified = false;
	}
	
	public synchronized void setAnimationFrame(int frame)
	{
		if(frame >= this.maxReelPos || frame < 0)
			return;
		this.reelPos = frame;
		currentFrame = currentReel[reelDirection][reelPos];
	}
	
	public synchronized BufferedImage getFrame()
	{
		return currentFrame;
	}
	
	public synchronized void setAnimationSpeed(int fps)
	{
		this.animation_speed = RenderMath.calculateAnimation(fps);
	}
	
	public synchronized void setReelDirection(int direction)
	{
		if(direction >= 0 && direction < directions_str.length
				&& direction != this.reelDirection)
		{
			this.reelDirection = direction;
			currentFrame = currentReel[reelDirection][reelPos];
		}
	}
	
	public int getReelDirection()
	{
		return this.reelDirection;
	}
	
	public boolean loadReels(String path) throws IOException
	{
		if(!path.endsWith(File.separator))
			path = path + File.separator;
		
		String[] reelNames = FileManager.getDirectories(path);
		
		boolean exists[] = new boolean[8];
		for(int x = 0;x < 8;x++)
			exists[x] = false;
		
		for(int i = 0;i < reelNames.length;i++)
		{
			String directionsPath = path + reelNames[i];
			String directionNames[] = FileManager.getDirectories(directionsPath);
			
			/* Is this a file we're looking for? */
			for(int x = 0;x < 8;x++)
				for(int y = 0;y < directionNames.length;y++)
					if(directions_str[x].equalsIgnoreCase(directionNames[y]))
					{
						exists[x] = true;
						break;
					}

			int x;
			for(x = 0;x < 8;x++)
			{
				if(!exists[x])
				{
					Log.vln("Reel " + reelNames[i] + " could not be imported: missing a directory.");
					break;
				}
			}
			if(x != 8) continue;
			
			String reelPath = path + reelNames[i] + File.separator;
			
			/* Get the reel count */
			String test_dir = reelPath + "front";
			String example_reels[] = FileManager.getFiles(test_dir);
			if(example_reels == null)
			{
				Log.vln("Reel " + reelNames[i] + " could not be imported: couldn't stat directory.");
				continue;
			}
			
			int reelCount = example_reels.length;
			if(reelCount > 1000)
			{
				Log.vln("Reel " + reelNames[i] + " could not be imported: too many reels! (MAX 1000)");
				continue;
			}
			
			BufferedImage[][] reels = new BufferedImage[8][reelCount];
			
			Log.vvln("Reel: " + reelNames[i] + " : " + example_reels.length + " frames.");
			
			for(x = 0;x < 8;x++)
			{
				String direction = directions_str[x];
				String dir = reelPath + direction + File.separator;
				
				String[] reel = FileManager.getFiles(dir);
				if(reel == null) break;
				
				if(reel.length != reelCount)
				{
					Log.vln("Reel " + reelNames[i] + " could not be imported: reel counts don't match" 
							+ " direction: " + direction + " length: " + reel.length);
					break;
				}
				
				/* Sort the reel so the images are in order */
				Arrays.sort(reel);
				
				/* Create a reel for this direction */
				BufferedImage[] direction_reel = new BufferedImage[reelCount];
				
				/* Load all of the images */
				for(int y = 0;y < reels[0].length;y++)
				{
					direction_reel[y] = assets.loadImage(dir + reel[y]);
					if(x == 0) Log.vln("\t" + reel[y]);
				}
				
				/* Set the reel for this direction */
				reels[x] = direction_reel;
			}
			
			if(x != 8) continue;
			
			map.put(reelNames[i], reels);
			Log.vln("Added reel " + reelNames[i]);
		}

		this.reelsPath = path;
		return true;
	}
	
	public void animation(double seconds)
	{
		if(animationQueue.isEmpty())
			return;
		
		Animation currentAnimation = animationQueue.getFirst();
		
		if(animation_speed <= 0)
			return;
		
		animation_total += seconds;
		while(animation_total >= animation_speed)
		{
			/* increment reelPos */
			reelPos++;
			
			if(reelPos >= maxReelPos)
			{
				reelPos = maxReelPos - 1;
				if(animation_loop)
				{
					reelPos = 0;
					setAnimationFrame(reelPos);
				} else {
					if(!endReelNotified && currentAnimation != null)
						currentAnimation.animationReelFinished();
					endReelNotified = true;
				}
			} else {
				currentFrame = currentReel[reelDirection][reelPos];
				setAnimationFrame(reelPos);
			}
			
			animation_total = 0;
		}
		
		if(currentAnimation != null)
			currentAnimation.doAnimation(seconds);
	}
	
	public void updateTransient(AssetManager assets, Game game, MMOCharacter character) throws IOException
	{
		this.assets = assets;
		this.character = character;
		map = new HashMap<String, BufferedImage[][]>();
		if(!loadReels(reelsPath))
			Log.v("FAILED TO LOAD REELS: " + reelsPath);
		if(!loadReel(currentReelName))
			Log.v("FAILED TO LOAD REEL: " + currentReelName);
		Log.vln("Character has " + animationQueue.size() + " animations.");
		Iterator<Animation> it = animationQueue.iterator();
		while(it.hasNext())
		{
			Animation animation = it.next();
			animation.updateTransient(game, null, character, this);
		}
		
	}
	
	protected transient MMOCharacter character; /**< The character we are managing. */
	protected transient AssetManager assets; /**< The asset manager to load assets from */
	protected transient BufferedImage currentFrame; /**< The current reel we are rendering */
	protected transient BufferedImage currentReel[][]; /**< Assets for the current reel that is playing */
	protected LinkedList<Animation> animationQueue; /**< The animation queue */
	protected String currentReelName; /**< The path of the current reel. */
	protected String reelsPath; /**< Where did we load our reels from? */
	protected int reelDirection; /**< Which direction the character is looking */
	protected int reelPos; /**< The current frame number */
	protected int maxReelPos; /**< The number of the last animation frame */
	protected boolean endReelNotified; /**< Whether or not the current animation has been notified */

	protected boolean animation_loop; /**< Whether or not to loop the reel */
	protected double animation_speed; /**< The amount of seconds in between frame numbers */
	protected double animation_total; /**< The amount of time spent on the current frame. */

	protected transient HashMap<String, BufferedImage[][]> map;

	/** Array for converting a direction to the resulting string (debug) */
	public static final String directions_str[] = {
			"front", "front_left", "front_right", "right", 
			"back", "back_right", "back_left", "left"
			};
	
	/** Direction definitions */
	public static final int FRONT = 0;
	public static final int FRONT_LEFT = 1;
	public static final int FRONT_RIGHT = 2;
	public static final int RIGHT = 3;
	public static final int BACK = 4;
	public static final int BACK_RIGHT = 5;
	public static final int BACK_LEFT = 6;
	public static final int LEFT = 7;
	
	private static final long serialVersionUID = -3092837263228127154L;
}
