package com.upl.mmorpg.game.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.Goblin;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.client.MapControl;
import com.upl.mmorpg.game.item.Item;
import com.upl.mmorpg.game.server.login.LoginManager;
import com.upl.mmorpg.game.server.login.LoginServerCallee;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.libnet.Server;
import com.upl.mmorpg.lib.libnet.ServerListener;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.map.GameMap;

public class ServerGame extends Game implements ServerListener
{
	public ServerGame(AssetManager assets, boolean headless)
	{
		super(assets, headless, true, true);

		if(!headless)
		{
			window = new JFrame("MMO Server Window");
			window.getContentPane().add(render);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setResizable(false);
			window.setVisible(true);

			control = new MapControl(render, maps[0], false);
			render.addMouseListener(control);
			render.addMouseMotionListener(control);
		}

		rpcs = new LinkedList<RPCManager>();
		clients = new LinkedList<GameStateManager>();

		render.startRender();
	}

	public boolean startServer()
	{
		if(server != null)
			return false;

		server = new Server(this, PORT);
		if(!server.startServer())
		{
			server.shutdown();
			server = null;
			return false;
		}

		return true;
	}

	public void stopServer()
	{
		Iterator<RPCManager> it = rpcs.iterator();
		while(it.hasNext())
			it.next().shutdown();

		rpcs.clear();
		server.shutdown();
		server = null;
	}

	@Override
	public void loadMaps() throws IOException
	{
		super.loadMaps();
		if(!headless)
			render.addBPRenderable(maps[0]);
	}

	@Override
	public void acceptClient(Socket socket, int cid) 
	{
		try 
		{
			RPCManager rpc = new RPCManager(socket, cid);
			LoginManager login = new LoginManager(this, rpc);
			LoginServerCallee callee = new LoginServerCallee(login);
			rpc.setCallee(callee);

			rpcs.add(rpc);
			Log.vnet("Client accepted.");
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void characterUpdated(MMOCharacter c)
	{
		/**
		 * Notify only the clients who care about this
		 * character getting updated.
		 */
		for(GameStateManager client : clients)
		{
			if(client.getCurrentMapID() == c.getCurrentMapID())
				client.updateCharacter(c.getUUID(), c);
		}
	}

	@Override
	public synchronized boolean pickupItem(MMOCharacter character, Item item)
	{
		boolean result = super.pickupItem(character, item);
		
		if(result)
		{
			for(GameStateManager client : clients)
			{
				if(client.getCurrentMapID() == character.getCurrentMapID())
					client.itemPickedUp(item, character.getUUID());
			}
		}

		return result;
	}
	
	@Override
	public synchronized boolean dropItem(MMOCharacter character, Item item)
	{
		boolean result = super.dropItem(character, item);
		
		if(result)
		{
			for(GameStateManager client : clients)
			{
				if(client.getCurrentMapID() == character.getCurrentMapID())
					client.itemDropped(character.getRow(), character.getColumn(), 
							item.getUUID(), character.getUUID());
			}
		}
		
		return result;
	}

	@Override
	public boolean addCharacter(MMOCharacter c, int map_id)
	{
		boolean result = super.addCharacter(c, map_id);
		this.characterUpdated(c);
		Log.vln("A new character has been added to the game on map " + map_id);
		return result;
	}

	public void addClient(GameStateManager client)
	{
		clients.add(client);
	}

	private JFrame window;
	private MapControl control;
	private Server server;

	private LinkedList<RPCManager> rpcs;
	private LinkedList<GameStateManager> clients;

	private static final int PORT = 8080;

	public static void main(String[] args) 
	{
		try 
		{
			final ServerGame g = new ServerGame(new AssetManager(), false);
			if(!g.startServer())
			{
				Log.e("Couldn't start server.");
				return;
			}
			g.loadAssets();
			g.loadMaps();

			/******** Wander example */
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

			/******** Item pickup example */
//			final Goblin collector = g.createGoblin(8, 8, GameMap.EXAMPLE1);
//			collector.walkTo(6, 6);
//			Runnable run = new Runnable()
//			{
//				public void run()
//				{
//					collector.pickupItem(6, 6, g.getItemsOnSquare(6, 6, GameMap.EXAMPLE1).iterator().next());
//				}
//			};
//			new Thread(run).start();
			
			/******** Item pickup and drop example */
			final Goblin collector = g.createGoblin(10, 6, GameMap.EXAMPLE1);
			Runnable run = new Runnable()
			{
				public void run()
				{
					collector.pickupItem(6, 6, g.getItemsOnSquare(6, 6, GameMap.EXAMPLE1).iterator().next());
					collector.addIdle(3000);
					collector.addDropItem(6, 6, g.getItemsOnSquare(6, 6, GameMap.EXAMPLE1).iterator().next());
					g.characterUpdated(collector);
				}
			};
			new Thread(run).start();


			/******** Defender/attacker example */
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

			/******** Walker example */

			//Goblin walker = g.createGoblin(8, 8, 0);
			//walker.walkTo(12, 12);
			//walker.addIdle(3000);
			//walker.addWalkTo(10, 10);
			//walker.addIdle(3000);
			//walker.addWalkTo(20, 20);
			//g.characterUpdated(walker);

			//Runnable run = new Runnable()
			//{
			//@Override
			//public void run()
			//{
			//try
			//{
			//Thread.sleep(5000);
			//walker.walkTo(6, 6);
			//Item i = g.getItemsOnSquare(6, 6, 0).iterator().next();
			//walker.pickupItem(6, 6, i);
			//g.characterUpdated(walker);
			//} catch(Exception e){}
			//}
			//};
			//new Thread(run).start();

			/******** Post-login spawn example */
			//Runnable run = new Runnable()
			//{
			//	@Override
			//public void run()
			//{
			//try
			//{
			//Thread.sleep(5000);
			//Goblin wanderer = g.createGoblin(10, 10, 0);
			//wanderer.walkTo(8, 8);
			//wanderer.addWalkTo(20, 20);
			//wanderer.addWalkTo(8, 8);
			//g.characterUpdated(wanderer);
			//} catch(Exception e){}
			//}
			//};
			//new Thread(run).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
