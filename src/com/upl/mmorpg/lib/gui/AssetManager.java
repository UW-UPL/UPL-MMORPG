package com.upl.mmorpg.lib.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;

/**
 * AssetManager helps reduce the amount of IO operations needed during
 * the game by caching the values of assets in hashmaps.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

public class AssetManager 
{
	/**
	 * Create a new AssetManager with no assets.
	 */
	public AssetManager()
	{
		images = new HashMap<String, BufferedImage>();
		files = new HashMap<String, FileManager>();
	}

	/**
	 * Get a file. If the file is already opened, it is returned immediately.
	 * @param path The path to the file that should be opened.
	 * @param create Whether or not the file should be created.
	 * @param r Whether or not you need read permission.
	 * @param w Whether or not you need write permission.
	 * @return The FileManager for the file.
	 * @throws IOException The IOException that occurred.
	 */
	public FileManager getFile(String path, boolean create, boolean r, boolean w) throws IOException
	{
		/* Is this file already open? */
		if(files.containsKey(path))
		{
			FileManager file = files.get(path);
			file.addReference();
			return file;
		}

		/* File not already open, open it */
		FileManager f = new FileManager(path, create, r, w);

		/* Were we able to open the file */
		if(!f.opened())
			return null;

		/* Add the new file manager to the hashmap */
		files.put(path, f);

		/* Return the new file */
		return f;
	}
	
	public void closeFile(FileManager file)
	{
		/* Close a reference to the file */
		file.close();
		
		/* Is the file still in use? */
		if(!file.opened())
			files.remove(file.getPath());
	}

	/**
	 * Load an image from a file. If the file is already loaded, it is returned immediately.
	 * @param path The path to load the file from.
	 * @return The BufferedImage representation of the image file.
	 * @throws IOException The IOException that occurred.
	 */
	public BufferedImage loadImage(String path) throws IOException
	{
		/* See if the image is already loaded */
		if(images.containsKey(path))
			return images.get(path);

		File file = null; /* The file we will open */
		BufferedImage result = null; /* The resulting image */

		try
		{ 
			/* Open the file */
			file = new File(path);
			/* Attempt to read the image from the file */
			result = ImageIO.read(file);
		}catch(IOException e)
		{
			Log.wtf("Image Not Found!", e);
			file = null;
			throw e;
		}
		
		if(result != null)
			images.put(path, result);

		file = null; /* close the file */

		return result;
	}

	public void releaseAll()
	{
		/* Release all of the images */
		images.clear();
		
		/* Release all of the file */
		Iterator<FileManager> f  = files.values().iterator();
		while(f.hasNext()) f.next().close();
		files.clear();
	}

	private HashMap<String, BufferedImage> images; /**< Cached/open images */
	private HashMap<String, FileManager> files; /**< Cached/open files s*/
}
