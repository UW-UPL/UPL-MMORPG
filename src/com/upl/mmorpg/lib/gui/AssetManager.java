package com.upl.mmorpg.lib.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class AssetManager 
{
	public AssetManager()
	{
		images = new HashMap<String, BufferedImage>();
	}
	
	public FileManager getFile(String path, boolean r, boolean w) throws IOException
	{
		FileManager f = new FileManager(path, true, r, w);
		if(!f.opened())
			return null;
		return f;
	}
	
	public BufferedImage loadImage(String path) throws IOException
	{
		if(images.containsKey(path))
			return images.get(path);
		
		File file = null;
		BufferedImage result = null;
		
		try
		{
			file = new File(path);
			result = ImageIO.read(file);
			
			if(result != null)
				images.put(path, result);
		}catch(IOException e)
		{
			Log.wtf("Image Not Found!", e);
			file = null;
			throw e;
		}
		
		file = null;
		
		return result;
	}
	
	public void releaseAll()
	{
		images.clear();
	}
	
	private HashMap<String, BufferedImage> images;
}
