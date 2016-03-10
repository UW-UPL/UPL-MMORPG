package com.upl.mmorpg.lib.animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderMath;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class AnimationManager
{
	public AnimationManager(AssetManager assets)
	{
		this.assets = assets;
		currentReel = null;
		reelPos = -1;
		reelDirection = FRONT;
		currentAnimation = null;
		currentFrame = null;
		animation_total = 0;
		animation_speed = 0;
		
		map = new HashMap<String, BufferedImage[][]>();
	}
	
	public boolean setReel(String reelName, boolean loop)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return false;;
		
		this.currentReel = reel;
		this.reelPos = 0;
		this.animation_total = 0;
		this.animation_loop = loop;
		this.maxReelPos = reel[this.reelDirection].length;
		
		this.currentFrame = reel[this.reelDirection][0];
		
		return true;
	}
	
	public int getMaxFrameForReel(String reelName)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return -1;
		return reel[0].length;
	}

	public synchronized void setAnimation(Animation animation)
	{
		if(this.currentAnimation != null)
			currentAnimation.animationInterrupted();
		this.currentAnimation = animation;
		animation.animationStarted();
		this.setAnimationFrame(0);
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
		if(direction > 0 && direction < directions_str.length)
		{
			this.reelDirection = direction;
			currentFrame = currentReel[reelDirection][reelPos];
		}
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

		return true;
	}
	
	public void animation(double seconds)
	{
		animation_total += seconds;
		if(animation_total >= animation_speed)
		{
			/* increment reelPos */
			reelPos++;
			
			if(reelPos >= maxReelPos)
			{
				reelPos = maxReelPos - 1;
				if(animation_loop)
				{
					reelPos = 0;
					currentFrame = currentReel[reelDirection][reelPos];
				} else {
					if(currentAnimation != null)
						currentAnimation.animationReelFinished();
				}
			} else {
				currentFrame = currentReel[reelDirection][reelPos];
			}
			
			animation_total = 0;
		}
		
		if(currentAnimation != null)
			currentAnimation.animation(seconds);
	}
	
	protected Animation currentAnimation;
	
	protected AssetManager assets;
	protected BufferedImage currentFrame;
	protected BufferedImage currentReel[][];
	protected int reelDirection;
	protected int reelPos;
	protected int maxReelPos;

	protected boolean animation_loop;
	protected double animation_speed;
	protected double animation_total;

	protected HashMap<String, BufferedImage[][]> map;

	public static final String directions_str[] = {
			"front", "front_left", "front_right", "right", 
			"back", "back_right", "back_left", "left"
			};
	
	public static final int FRONT = 0;
	public static final int FRONT_LEFT = 1;
	public static final int FRONT_RIGHT = 2;
	public static final int RIGHT = 3;
	public static final int BACK = 4;
	public static final int BACK_RIGHT = 5;
	public static final int BACK_LEFT = 6;
	public static final int LEFT = 7;
}
