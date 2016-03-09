package com.upl.mmorpg.lib.animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class Animation
{
	public Animation(AssetManager assets)
	{
		this.assets = assets;
		currentReel = null;
		currentReelPos = -1;
		animation_speed = 0;
		animation_total = 0;
		
		map = new HashMap<String, BufferedImage[][]>();
	}

	public void setReel(String reelName, long nanos)
	{
		BufferedImage[][] reel = map.get(reelName);
		if(reel == null) return;
		
		this.currentReel = reel;
		this.currentReelPos = 0;
		this.animation_total = 0;
		this.animation_speed = nanos;
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
			Log.vln(reelNames[i] + " has " + reelCount + " images.");
			if(reelCount > 1000)
			{
				Log.vln("Reel " + reelNames[i] + " could not be imported: too many reels! (MAX 1000)");
				continue;
			}
			
			BufferedImage[][] reels = new BufferedImage[8][reelCount];
			
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
				for(int y = 0;y < reels.length;y++)
					direction_reel[y] = assets.loadImage(dir + reel[y]);
				
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
		
		if(animation_total * 1000000000 >= animation_speed)
		{
			currentReelPos++;
			if(currentReelPos >= maxReelPos)
				currentReelPos = 0;
		}
	}

	protected AssetManager assets;
	protected BufferedImage currentReel[][];
	protected int currentReelPos;
	protected int maxReelPos;

	protected long animation_speed;
	protected double animation_total;

	protected HashMap<String, BufferedImage[][]> map;

	private static final String directions_str[] = {
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
	
	public static void main(String args[])
	{
		Animation ani = new Animation(new AssetManager());
		try {
			ani.loadReels("assets/models/goblin");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
