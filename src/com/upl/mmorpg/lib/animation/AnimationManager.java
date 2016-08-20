package com.upl.mmorpg.lib.animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderMath;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class AnimationManager implements Serializable
{
	public AnimationManager(AssetManager assets, Game game, MMOCharacter character)
	{
		this.assets = assets;
		this.character = character;
		currentReel = null;
		reelPos = -1;
		reelDirection = FRONT;
		currentFrame = null;
		animation_total = 0;
		animation_speed = 0;
		animating = true;
		currentReelName = null;
		this.reelsPath = null;
		animationQueue = new AnimationQueue(this, new IdleAnimation(game, this, character, -1));
		
		map = new HashMap<String, BufferedImage[][]>();
	}
	
	/**
	 * Reset the animation manager so that it continues animating.
	 */
	protected void animationChanged()
	{
		animating = true;
	}
	
	/**
	 * Set the current reel.
	 * @param reelName The name of the reel.
	 * @param loop Whether or not to loop the reel when it ends.
	 * @return Whether or not that reel exists.
	 */
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
		this.animating = true;
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
		animating = true;
		currentReelName = reelName;
		
		currentFrame = reel[reelDirection][reelPos];
		
		return true;
	}
	
	/**
	 * Returns the amount of frames in the reel.
	 * @param reelName The name of the reel.
	 * @return The number of reels in the frame
	 */
	public int getFrameCountForReel(String reelName)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return -1;
		return reel[0].length;
	}

	/**
	 * Add an animation to the animation queue.
	 * @param animation The animation to add to the queue.
	 */
	public synchronized void addAnimation(Animation animation)
	{
		animationQueue.add(animation);
		Log.vvln("Animation " + animation + " added to animation manager");
	}
	
	/**
	 * Transition to the given animation. The current animation will
	 * be given an opportunity to finish.
	 * @param animation The animation to transition to.
	 */
	public void transitionTo(Animation animation)
	{
		animationQueue.transitionTo(animation);
	}
	
	/**
	 * Cycle to the next animation. If there aren't any animations left,
	 * the default animation will be played.
	 */
	public void nextAnimation()
	{
		animationQueue.nextAnimation();
	}
	
	/**
	 * Set the current frame number.
	 * @param frame The frame number to set.
	 */
	public synchronized void setAnimationFrame(int frame)
	{
		if(frame >= this.maxReelPos || frame < 0)
			return;
		this.reelPos = frame;
		currentFrame = currentReel[reelDirection][reelPos];
	}
	
	/**
	 * Get the currently displayed frame.
	 * @return
	 */
	public synchronized BufferedImage getFrame()
	{
		return currentFrame;
	}
	
	/**
	 * Set the speed at which to animate the reel.
	 * @param fps The amount of frames to display per second.
	 */
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
					Log.e("Reel " + reelNames[i] + " could not be imported: missing a directory.");
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
				Log.e("Reel " + reelNames[i] + " could not be imported: couldn't stat directory.");
				continue;
			}
			
			int reelCount = example_reels.length;
			if(reelCount > 1000)
			{
				Log.e("Reel " + reelNames[i] + " could not be imported: too many reels! (MAX 1000)");
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
					Log.e("Reel " + reelNames[i] + " could not be imported: reel counts don't match" 
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
					if(x == 0) Log.vvln("\t" + reel[y]);
				}
				
				/* Set the reel for this direction */
				reels[x] = direction_reel;
			}
			
			if(x != 8) continue;
			
			map.put(reelNames[i], reels);
			Log.vvln("Added reel " + reelNames[i]);
		}

		this.reelsPath = path;
		return true;
	}
	
	public void animation(double seconds)
	{
		if(!animating) return;
		
		Animation currentAnimation = animationQueue.getCurrent();
		
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
					animating = false;
					if(currentAnimation != null)
						currentAnimation.animationReelFinished();
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
			Log.e("FAILED TO LOAD REELS: " + reelsPath);
		if(!loadReel(currentReelName))
			Log.e("FAILED TO LOAD REEL: " + currentReelName);
		Log.vvln("Character has " + animationQueue.size() + " animations.");
		Iterator<Animation> it = animationQueue.iterator();
		while(it.hasNext())
		{
			Animation animation = it.next();
			animation.updateTransient(game, character, this);
		}
		
	}
	
	protected transient MMOCharacter character; /**< The character we are managing. */
	protected transient AssetManager assets; /**< The asset manager to load assets from */
	protected transient BufferedImage currentFrame; /**< The current reel we are rendering */
	protected transient BufferedImage currentReel[][]; /**< Assets for the current reel that is playing */
	protected AnimationQueue animationQueue; /**< The queue for our animations, which is custom. */
	protected String currentReelName; /**< The path of the current reel. */
	protected String reelsPath; /**< Where did we load our reels from? */
	protected int reelDirection; /**< Which direction the character is looking */
	protected int reelPos; /**< The current frame number */
	protected int maxReelPos; /**< The number of the last animation frame */
	protected boolean animating; /**< Whether or not we can animate right now */

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
