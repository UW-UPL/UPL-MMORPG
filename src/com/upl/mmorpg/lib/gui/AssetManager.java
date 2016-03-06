package com.upl.mmorpg.lib.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.upl.mmorpg.lib.liblog.Log;

public class AssetManager 
{
	public AssetManager()
	{
		files = new ArrayList<File>();
		images = new ArrayList<BufferedImage>();
	}
	
	public BufferedImage loadImage(String path) throws IOException
	{
		File file = null;
		BufferedImage result = null;
		try
		{
			file = new File(path);
			result = ImageIO.read(file);
			
			files.add(file);
			images.add(result);
		}catch(IOException e)
		{
			Log.wtf("Image Not Found!", e);
			file = null;
			throw e;
		}
		
		return result;
	}
	
	public void releaseImage(BufferedImage img)
	{
		if(!images.contains(img))
			return;
		files.remove(images.indexOf(img));
		images.remove(img);
	}
	
	public void releaseAll()
	{
		for(int x = 0;x < files.size();x++)
		{
			files.set(x, null);
			images.set(x, null);
		}
		
		files.clear();
		images.clear();
	}
	
	public BufferedImage getImage(int num)
	{
		return images.get(num);
	}
	
	private ArrayList<File> files;
	private ArrayList<BufferedImage> images;
}
