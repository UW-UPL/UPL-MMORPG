package com.upl.mmorpg.game.server;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class MapControl implements MouseMotionListener, MouseListener, Runnable {
	public MapControl(RenderPanel render, Grid2DMap map, boolean autoscroll) {
		this.render = render;
		this.autoscroll = autoscroll;

		moveUp = false;
		moveDown = false;
		moveRight = false;
		moveLeft = false;
		dragging = false;

		lastX = 0;
		lastY = 0;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!dragging)
			return;

		double x = e.getX();
		double y = e.getY();

		render.moveView(lastX - x, lastY - y);

		lastX = x;
		lastY = y;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dragging = true;
		double x = e.getX();
		double y = e.getY();

		lastX = x;
		lastY = y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
	}

	@Override
	public void run() {
		while (moving) {
			try {
				Thread.sleep(1);
				if (moveRight)
					render.moveView(.5, 0);
				if (moveLeft)
					render.moveView(-.5, 0);
				if (moveUp)
					render.moveView(0, -.5);
				if (moveDown)
					render.moveView(0, .5);
			} catch (Exception e) {}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!autoscroll)
			return;
		double x = e.getX();
		double y = e.getY();
		boolean found = false;

		if (x - render.getWidth() + 25 > 0) {
			moveRight = true;
			startMovement();
			found = true;
		}
		
		if (x + render.getWidth() - 25 < render.getWidth()) {
			moveLeft = true;
			startMovement();
			found = true;
		}
		
		if (y - render.getHeight() + 25 > 0) {
			moveDown = true;
			startMovement();
			found = true;
		}
		
		if (y + render.getHeight() - 25 < render.getHeight()) {
			moveUp = true;
			startMovement();
			found = true;
		}
		
		if(!found)
		{
			moving = false;
			moveDown = false;
			moveUp = false;
			moveRight = false;
			moveLeft = false;
			stopMovement();
		}
	}

	public void startMovement() {
		if (moving || !autoscroll)
			return;

		moving = true;
		moveThread = new Thread(this);
		moveThread.start();
	}

	public void stopMovement() {
		moving = false;
		
		try
		{
			moveThread.interrupt();
		} catch(Exception e) {}
		
		try
		{
			moveThread.join(1000);
		} catch(Exception e){}

		moveThread = null;
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}

	private Thread moveThread; /**< Thread for moving screen */
	private boolean moveRight;
	private boolean moveLeft;
	private boolean moveUp;
	private boolean moveDown;
	private boolean moving;
	private boolean autoscroll;
	private double lastX;
	private double lastY;
	private boolean dragging;
	private RenderPanel render;
}
