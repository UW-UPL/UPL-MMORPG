package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.upl.mmorpg.lib.collision.Collidable;
import com.upl.mmorpg.lib.collision.CollisionManager;
import com.upl.mmorpg.lib.liblog.Log;

public class RenderPanel extends JPanel implements Runnable
{
	public RenderPanel(boolean vsync, boolean showfps, boolean headless)
	{
		this.headless = headless;
		/* Setup vsync */
		if(vsync)
		{
			this.vsync = RenderMath.calculateVSYNC(FRAMES_PER_SECOND);
		} else {
			this.vsync = 0;
		}

		viewX = 0;
		viewY = 0;
		/* Initilize panels */
		backPane = new LinkedList<Renderable>();
		midPane = new LinkedList<Renderable>();
		glassPane = new LinkedList<Renderable>();

		/* Initilize collision manager */
		collision_manager = new CollisionManager();

		if(showfps && !headless)
		{
			fps = new FPSMeasure(this);
			fps.startMeasuring();
			glassPane.add(fps);
		} else {
			fps = null;
		}

		/* Set the size of the frame in the parent */
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

		/* Make this panel visible */
		this.setVisible(true);
	}
	
	public void addGuideLines()
	{
		ExampleLine line1 = new ExampleLine(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
		ExampleLine line2 = new ExampleLine(0, PANEL_HEIGHT, PANEL_WIDTH, 0);

		glassPane.add(line1);
		glassPane.add(line2);
	}

	/**
	 * Stop the rendering loop
	 */
	public void stopRender()
	{
		rendering = false;
		try
		{
			renderThread.interrupt();
		}catch(Exception e){}

		try
		{
			renderThread.join(1000);
		}catch(Exception e){}

		renderThread = null;

		if(fps != null)
			fps.stopMeasuring();
	}

	/**
	 * Start the rendering loop
	 */
	public void startRender()
	{
		if(rendering) return;
		/* Setup rendering thread */
		rendering = true;
		renderThread = new Thread(this);
		renderThread.start();

		if(fps != null)
			fps.startMeasuring();
	}

	public void addCollidable(Collidable collidable)
	{
		collidable.setCollisionManager(collision_manager);
		collision_manager.addCollidable(collidable);
	}

	public void removeCollidable(Collidable collidable)
	{
		collidable.setCollisionManager(collision_manager);
		collision_manager.removeCollidable(collidable);
	}

	public void addBounds(Collidable collidable)
	{
		collidable.setCollisionManager(collision_manager);
		collision_manager.addBounds(collidable);
	}

	public void removeBounds(Collidable collidable)
	{
		collidable.setCollisionManager(collision_manager);
		collision_manager.removeBounds(collidable);
	}

	public synchronized void addRenderable(Renderable render)
	{
		midPane.add(render);
	}

	public synchronized void removeRenderable(Renderable render)
	{
		midPane.remove(render);
	}

	public synchronized void removeAllRenderable()
	{
		midPane.clear();
	}

	public synchronized void addGPRenderable(Renderable render)
	{
		glassPane.add(render);
	}

	public synchronized void removeGPRenderable(Renderable render)
	{
		glassPane.remove(render);
	}

	public synchronized void removeAllGPRenderable()
	{
		glassPane.clear();
	}

	public synchronized void addBPRenderable(Renderable render)
	{
		backPane.add(render);
	}

	public synchronized void removeBPRenderable(Renderable render)
	{
		backPane.remove(render);
	}

	public synchronized void removeAllBPRenderable()
	{
		backPane.clear();
	}

	public synchronized void removeAll()
	{
		glassPane.clear();
		midPane.clear();
		backPane.clear();
	}

	public synchronized void removeAll(Renderable transition)
	{
		glassPane.clear();
		midPane.clear();
		backPane.clear();
		backPane.add(transition);
	}

	public void loadAllImages() throws IOException
	{
		Iterator<Renderable> it = glassPane.iterator();
		while(it.hasNext())
			it.next().loadImages();
		it = midPane.iterator();
		while(it.hasNext())
			it.next().loadImages();
		it = backPane.iterator();
		while(it.hasNext())
			it.next().loadImages();
	}

	private void renderPane(LinkedList<Renderable> pane, Graphics2D g, double seconds)
	{
		Iterator<Renderable> it = pane.iterator();
		while(it.hasNext())
		{
			Renderable render = it.next();
			Log.vrndln("Rendering component: " + render.getRenderName());
			if(render.hasAnimation)
				render.animation(seconds);
			render.render(g);
		}
	}
	
	private void renderPaneHeadless(LinkedList<Renderable> pane, double seconds)
	{
		Iterator<Renderable> it = pane.iterator();
		while(it.hasNext())
		{
			Renderable render = it.next();
			Log.vrndln("Rendering component: " + render.getRenderName());
			if(render.hasAnimation)
				render.animation(seconds);
		}
	}
	
	private synchronized void renderAll(Graphics2D g, double seconds)
	{
		/* Move to the proper spot on the canvas */
		int vx = (int)-getViewX();
		int vy = (int)-getViewY();
		
		Graphics2D notransform = (Graphics2D)g.create();
		g.translate(vx, vy);
		renderPane(backPane, (Graphics2D)g.create(), seconds);
		renderPane(midPane, (Graphics2D)g.create(), seconds);
		renderPane(glassPane, notransform, seconds);
	}
	
	private synchronized void renderAllHeadless(double seconds)
	{
		renderPaneHeadless(backPane, seconds);
		renderPaneHeadless(midPane, seconds);
		renderPaneHeadless(glassPane, seconds);
	}
	
	public synchronized void setView(double viewX, double viewY)
	{
		this.viewX = viewX;
		this.viewY = viewY;
	}
	
	public synchronized void moveView(double diffX, double diffY)
	{
		this.viewX += diffX;
		this.viewY += diffY;
	}
	
	public synchronized double getViewX() {return viewX;}
	public synchronized double getViewY() {return viewY;}

	public void paintComponent(Graphics g)
	{
			/* Call JPanel's paint component. */
			super.paintComponent(g);

			if(lastRender == 0)
				lastRender = System.nanoTime();

			long nano_change = System.nanoTime() - lastRender;
			double seconds_change = (double)nano_change / (double)1000000000.0d;

			/* Do our own rendering */
			Graphics2D g2 = (Graphics2D)g;
			renderAll(g2, seconds_change);
			g.dispose();

			increment_fps();
			lastRender = System.nanoTime();
	}
	
	public void headlessRender()
	{
		if(lastRender == 0)
			lastRender = System.nanoTime();

		long nano_change = System.nanoTime() - lastRender;
		double seconds_change = (double)nano_change / (double)1000000000.0d;

		/* Do our own rendering */
		renderAllHeadless(seconds_change);

		increment_fps();
		lastRender = System.nanoTime();
	}

	/* Draw loop */
	@Override
	public void run()
	{
		while(rendering)
		{
			try
			{
				if(vsync > 0)
					Thread.sleep(vsync);

				/* Are we still rendering after that sleep? */
				if(!rendering) break;

				if(headless)
				{
					headlessRender();
				} else {
					/* Render another frame */
					this.repaint();
				}
			}catch(Exception e)
			{
				Log.wtf("Rendering failed!", e);
			}
		}
	}

	public int getWidth()
	{
		return PANEL_WIDTH;
	}

	public int getHeight()
	{
		return PANEL_HEIGHT;
	}

	private synchronized int reset_fps()
	{
		int tmp;
		synchronized(this)
		{
			tmp = frames_per_second;
			frames_per_second = 0;
		}
		return tmp;
	}
	
	private synchronized void increment_fps()
	{
		frames_per_second++;
	}
	
	/* Some statistics stuff */
	private int frames_per_second; /* fps counter */
	private FPSMeasure fps;

	private int vsync; /* The amount to sleep for vsync */
	private Thread renderThread;
	private boolean rendering;
	private boolean headless;
	private long lastRender;
	private LinkedList<Renderable> glassPane;
	private LinkedList<Renderable> midPane;
	private LinkedList<Renderable> backPane;
	private CollisionManager collision_manager;
	
	private double viewX;
	private double viewY;

	private static final int PANEL_WIDTH = 800;
	private static final int PANEL_HEIGHT = 600;

	private static final int FRAMES_PER_SECOND = 100;

	private static final boolean CENTER_LINES = false;

	private static final long serialVersionUID = -3812924750709550502L;

	private class FPSMeasure extends TextView implements Runnable
	{
		public FPSMeasure(RenderPanel panel)
		{
			super("FPS: XX");
			this.panel = panel;
		}

		@Override
		public void render(Graphics2D g) 
		{
			g.setColor(Color.red);
			g.drawString(getText(), (float)locX, (float)locY);
		}

		@Override
		public String getRenderName() {return "FPS clock";}

		@Override
		public void run() 
		{
			while(measuring)
			{
				try
				{
					/* Sleep for one second */
					Thread.sleep(1000);
					setFPS(panel.reset_fps());
				}catch(Exception e)
				{
					break;
				}
			}
		}

		public void stopMeasuring()
		{
			measuring = false;
			try
			{
				framesThread.interrupt();
			}catch(Exception e){}

			try
			{
				framesThread.join(1000);
			}catch(Exception e){}

			framesThread = null;
		}

		public void startMeasuring()
		{
			if(measuring) return;

			measuring = true;
			framesThread = new Thread(this);
			framesThread.start();
		}

		private synchronized void setFPS(int fps)
		{
			this.setText("FPS: " + fps);
			this.setX(0);
			this.setY(this.getHeight());
		}

		private Thread framesThread; /* Thread for measuring fps */
		private boolean measuring; /* whether or not we are generating stats */
		private RenderPanel panel;
	}
}
