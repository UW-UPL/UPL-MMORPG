package com.upl.mmorpg.lib.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

public class AssetManager 
{
	public AssetManager()
	{
		image_files = new ArrayList<File>();
		images = new ArrayList<BufferedImage>();
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
		File file = null;
		BufferedImage result = null;
		try
		{
			file = new File(path);
			result = ImageIO.read(file);
			
			image_files.add(file);
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
		image_files.remove(images.indexOf(img));
		images.remove(img);
	}
	
	public void releaseAll()
	{
		for(int x = 0;x < image_files.size();x++)
		{
			image_files.set(x, null);
			images.set(x, null);
		}
		
		image_files.clear();
		images.clear();
	}
	
	private ArrayList<File> image_files;
	private ArrayList<BufferedImage> images;
}
