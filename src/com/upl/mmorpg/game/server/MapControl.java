package com.upl.mmorpg.game.server;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class MapControl implements MouseMotionListener, MouseListener
{
	public MapControl(RenderPanel render, Grid2DMap map)
	{
		this.render = render;
		this.map = map;
		dragging = false;
		lastX = 0;
		lastY = 0;
	}
	
	@Override 
	public void mouseDragged(MouseEvent e) 
	{
		if(!dragging) return;
		
		double x = e.getX();
		double y = e.getY();
		
		render.moveView(lastX - x, lastY - y);
		
		lastX = x;
		lastY = y;
	}
	
	@Override 
	public void mousePressed(MouseEvent e) 
	{
		dragging = true;
		double x = e.getX();
		double y = e.getY();
		
		lastX = x;
		lastY = y;
	}
	
	@Override 
	public void mouseReleased(MouseEvent e) 
	{
		dragging = false;
	}
	
	@Override public void mouseMoved(MouseEvent e) {}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	private double lastX;
	private double lastY;
	private boolean dragging;
	private RenderPanel render;
	private Grid2DMap map;
}
