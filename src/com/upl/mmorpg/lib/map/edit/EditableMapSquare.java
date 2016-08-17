package com.upl.mmorpg.lib.map.edit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.MapSquare;

public class EditableMapSquare extends MapSquare 
{
	public EditableMapSquare(int row, int col,
			String image_name, String overlay_name,
			String destroyed_overlay_name) 
	{
		super(row, col, image_name, overlay_name, destroyed_overlay_name);
	}
	
	@Override
	public void render(Graphics2D g, RenderPanel panel) 
	{
		super.render(g, panel);
		
		double line_width = 1.0d / 8.0d;
		if(!this.passThrough && false)
		{
			g.setColor(Color.BLUE);
			
			fillRect(panel, g, locX, locY, line_width, height);
			fillRect(panel, g, locX, locY, width, line_width);
			fillRect(panel, g, locX + width - line_width, locY, line_width, height);
			fillRect(panel, g, locX, locY + height - line_width, width, line_width);
		}
		
		if(this.destructible)
		{
			g.setColor(Color.RED);
			double offset = line_width;
			fillRect(panel, g, locX + offset, locY + offset, line_width, height - (offset * 2));
			fillRect(panel, g, locX + offset, locY + offset, width - (offset * 2), line_width);
			fillRect(panel, g, locX + width - line_width - offset, locY + offset, line_width, height - (offset * 2));
			fillRect(panel, g, locX + offset, locY + height - line_width - offset, width - (offset * 2), line_width);
		}
		
		if(this.isLinkLanding)
			drawImage(panel, g, linkLandingImage, locX, locY, width, height);
		
		if(this.isMapLink)
			drawImage(panel, g, mapLinkImage, locX, locY, width, height);
	}
	
	public MapSquare export()
	{
		return new MapSquare(row, col, image_name, overlay_name, destroyed_overlay_name);
	}
	
	@Override
	public void loadImages(AssetManager assets) throws IOException
	{
		super.loadImages(assets);
		
		linkLandingImage = assets.loadImage("assets/images/editor/landingTool.png");
		mapLinkImage = assets.loadImage("assets/images/editor/linkTool.png");
	}

	public void setImage(String image_name)
	{
		this.image_name = image_name;
	}
	
	public void setOverlay(String overlay_name)
	{
		this.overlay_name = overlay_name;
	}
	
	public void setDestroyedOverlay(String destroyed_overlay_name)
	{
		this.destroyed_overlay_name = destroyed_overlay_name;
	}
	
	public void setPassThrough(boolean passThrough)
	{
		this.passThrough = passThrough;
	}
	
	public synchronized boolean isPassThrough()
	{
		if(isDestroyed())
		{
			return passThroughWhenDestroyed;
		} else return this.passThrough;
	}
	
	public void setDestructible(boolean destructible)
	{
		this.destructible = destructible;
	}
	
	public void setMapLink(String mapname, int row, int col)
	{
		isMapLink = true;
		linked_map = mapname;
		link_row = row;
		link_col = col;
	}
	
	public void disableMapLink()
	{
		isMapLink = false;
		linked_map = null;
		link_row = -1;
		link_col = -1;
	}
	
	public void setLanding(boolean isLinkLanding)
	{
		this.isLinkLanding = isLinkLanding;
	}
	
	public boolean isLinkLanding()
	{
		return isLinkLanding;
	}
	
	private transient BufferedImage linkLandingImage;
	private transient BufferedImage mapLinkImage;
	
	private static final long serialVersionUID = 454306166949201369L;
}
