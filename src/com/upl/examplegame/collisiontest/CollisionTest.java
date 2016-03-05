package com.upl.examplegame.collisiontest;

import java.awt.Color;
import java.io.IOException;

import com.upl.mmorpg.lib.collision.CollisionManager;
import com.upl.mmorpg.lib.gui.ExampleBox;
import com.upl.mmorpg.lib.gui.ExampleCircle;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.RenderWindow;

public class CollisionTest 
{
	public CollisionTest()
	{
		window = new RenderWindow();
		panel = window.getPanel();

		ExampleBox bounding = new ExampleBox(0, 0, panel.getWidth(), panel.getHeight());
		bounding.setColor(Color.BLUE);
//		Random r = new Random(System.nanoTime());
//		for(int x = 0;x < 5000;x++)
//			if(r.nextInt(2) == 0)
//				addBox();
//			else addCircle();
		for(int row = 1;row < 10;row++)
		{
			for(int col = 1;col < 8;col++)
			{
				addBox(row * 75, col * 75);
			}
		}
		
		//panel.addRenderable(bounding);
		panel.addBounds(bounding);

		try 
		{
			window.show();
		} catch (IOException e) 
		{
			e.printStackTrace();
			shutdown();
		}
	}
	
	public void addBox(double x, double y)
	{
		ExampleBox box = new ExampleBox(0, 0, 50, 50, collisions);
		box.setCenter(x, y);
		box.enableAnimation();
		
		panel.addRenderable(box);
		panel.addCollidable(box);
	}
	
	public void addCircle(double x, double y)
	{
		ExampleCircle circle = new ExampleCircle(0, 0, 25, collisions);
		circle.setCenter(x, y);
		circle.enableAnimation();
		
		panel.addRenderable(circle);
		panel.addCollidable(circle);
	}
	
	private void shutdown()
	{
		try {window.dispose();} catch(Exception e){}
		window = null;
		panel = null;
	}
	
	private RenderWindow window;
	private RenderPanel panel;
	private CollisionManager collisions;
	
	public static void main(String[] args) 
	{
		new CollisionTest();
	}
}
