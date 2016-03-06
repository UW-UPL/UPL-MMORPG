package com.upl.mmorpg.lib.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class IsoMapSquare extends Renderable
{
	public IsoMapSquare(double x, double y, double width, double height,
			AssetManager assets, String image_name)
	{
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.assets = assets;
		this.image_name = image_name;
	}
	
	@Override
	public void loadImages() throws IOException
	{
		int fd = assets.loadImage(image_name);
		image = assets.getImage(fd);
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		g.drawImage(image, (int)locX, (int)locY, 
				(int)width, (int)height, null);
	}

	@Override
	public String getRenderName() 
	{
		return "Map Square: " + image_name;
	}
	
	private AssetManager assets;
	private String image_name;
	private BufferedImage image;
}
