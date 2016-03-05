package com.upl.examplegame.collisiontest;

import java.awt.Color;
import java.io.IOException;

import com.upl.mmorpg.lib.collision.CollisionManager;
import com.upl.mmorpg.lib.gui.ExampleBox;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.RenderWindow;

public class CollisionTest 
{
	public CollisionTest()
	{
		window = new RenderWindow();
		panel = window.getPanel();
		collisions = new CollisionManager();

		ExampleBox box = new ExampleBox(50, 50, 50, 50, collisions);
		box.enableAnimation();

		try 
		{
			window.show();
		} catch (IOException e) 
		{
			e.printStackTrace();
			shutdown();
		}
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
		
	}

}
