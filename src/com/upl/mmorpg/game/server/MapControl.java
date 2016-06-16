package com.upl.mmorpg.game.server;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class MapControl implements MouseMotionListener, MouseListener, Runnable {
	public MapControl(RenderPanel render, Grid2DMap map) {
		this.render = render;
		this.map = map;

		mapWidth = this.map.getWidth();
		mapHeight = this.map.getHeight();

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

		double x = e.getX();
		double y = e.getY();

		if (x - render.getWidth() + 25 > 0) {
			moveRight = true;
			startMovement();
		} else if (x + render.getWidth() - 25 < render.getWidth()) {
			moveLeft = true;
			startMovement();
		} else if (y - render.getHeight() + 25 > 0) {
			moveDown = true;
			startMovement();
		} else if (y + render.getHeight() - 25 < render.getHeight()) {
			moveUp = true;
			startMovement();
		} else {
			moving = false;
			moveDown = false;
			moveUp = false;
			moveRight = false;
			moveLeft = false;
			stopMovement();
		}

	}

	public void startMovement() {

		if (moving)
			return;

		moving = true;
		moveThread = new Thread(this);
		moveThread.start();

	}

	public void stopMovement() {
		moving = false;
		try{
			moveThread.interrupt();
		}catch(Exception e) {}
		
		try{
			moveThread.join(1000);
		}catch(Exception e){}

		moveThread = null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		System.out.print("mouseX: " + e.getX() + "\nmouseY: " + e.getY() + "\n");
		System.out.print(e.getX() - render.getWidth() + 50 + "\n");
		System.out.print(render.getWidth() + "\n");

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private Thread moveThread; /* For moving screen */
	private boolean moveRight;
	private boolean moveLeft;
	private boolean moveUp;
	private boolean moveDown;
	private boolean moving;
	private double lastX;
	private double lastY;
	private double mapWidth;
	private double mapHeight;
	private boolean dragging;
	private RenderPanel render;
	private Grid2DMap map;

}
