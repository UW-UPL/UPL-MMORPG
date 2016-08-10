package com.upl.mmorpg.game.server;

import java.io.IOException;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.map.Grid2DMap;

public class ServerGame extends Game
{
	public ServerGame(String map_path, AssetManager assets, boolean headless)
	{
		super(map_path, assets, headless, true, true);
		
		map = new Grid2DMap(render, TILE_SIZE);
		control = new MapControl(render, map);
		render.addMouseListener(control);
		render.addMouseMotionListener(control);
		
		
		if(!headless)
		{
			window = new JFrame("MMO Server Window");
			window.getContentPane().add(render);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setResizable(false);
			window.setVisible(true);
		}
		
		render.startRender();
	}
	
	@Override
	public void loadMap() throws IOException
	{
		super.loadMap();
		if(!headless)
			render.addBPRenderable(map);
	}

	private JFrame window;
	private MapControl control;
	
	public static void main(String[] args) 
	{
		try 
		{
			final ServerGame g = new ServerGame("assets/maps/example.mmomap", 
					new AssetManager(), false);
			g.loadAssets();
			g.loadMap();
//			for(int row = 7;row < 13;row++)
//			{
//				for(int col = 9;col < 15;col++)
//				{
//					Goblin gob1 = g.createGoblin(row, col);
//					gob1.wander(5);
//				}
//			}
			
//			final Goblin wanderer = g.createGoblin(11, 11);
//			final Goblin follower = g.createGoblin(11, 20);
//			follower.follow(wanderer);
//			wanderer.wander(5);
//			
//			Runnable run = new Runnable()
//			{
//				public void run()
//				{
//					try { Thread.sleep(10000); } catch(Exception e){}
//					wanderer.wander(10);
//				}
//			};
//			new Thread(run).start();
			
			final Goblin collector = g.createGoblin(12, 12);
			collector.walkTo(14, 9);
			Runnable run = new Runnable()
			{
				public void run()
				{
					try { Thread.sleep(4500);} catch(Exception e) {}
					g.pickupItem(collector, g.getItemsOnSquare(14, 9).iterator().next());
				}
			};
			new Thread(run).start();
			
			//Goblin defender = g.createGoblin(11, 10);
			//defender.wander(5);
			//Goblin attacker = g.createGoblin(11, 11);
			//attacker.attack(defender);
//			final Goblin die = g.createGoblin(12, 12);
//			Runnable run = new Runnable()
//			{
//				public void run()
//				{
//					try { Thread.sleep(2000); } catch(Exception e){}
//					die.die();
//				}
//			};
//			new Thread(run).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
