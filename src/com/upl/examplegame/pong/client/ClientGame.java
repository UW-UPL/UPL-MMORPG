package com.upl.examplegame.pong.client;

import java.awt.Color;
import java.io.IOException;

import com.upl.examplegame.pong.Game;
import com.upl.examplegame.pong.Player;
import com.upl.mmorpg.lib.gui.RenderPanel;
import com.upl.mmorpg.lib.gui.RenderWindow;
import com.upl.mmorpg.lib.gui.TextView;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class ClientGame extends Game
{
	public ClientGame() throws IOException 
	{
		super();
	}

	public void setupWindow() throws IOException
	{
		window = new RenderWindow();
		panel = window.getPanel();

		puck.hide();
		panel.addRenderable(puck);
		panel.addCollidable(puck);
		panel.addBPRenderable(board);
		panel.addBounds(board);

		/* Put a prompt on the screen */
		currentPrompt = new TextView("Waiting for another player to join...");
		currentPrompt.setFontSize(26);
		currentPrompt.setCenter(board.getWidth() / 2, board.getHeight() / 2);
		currentPrompt.setBackground(Color.WHITE);

		panel.addGPRenderable(currentPrompt);

		window.show();
	}

	public boolean connect()
	{
		try 
		{
			rpc = new RPCManager(SERVER_ADDRESS, SERVER_PORT, new PongRPCCallee(this));
			call = new PongRPCCaller(rpc);
		} catch (IOException e) 
		{
			rpc = null;
			call = null;
			return false;
		}

		return true;
	}

	public void otherPlayerPaddle(float x, float y)
	{
		opponent.getPaddle().setCenter(x, y);
	}

	public void youScored()
	{
		me.setScore(me.getScore() + 1);
	}

	public void otherPlayerScored()
	{
		opponent.setScore(opponent.getScore() + 1);
	}

	public void setPuckProperties(float x, float y, 
			float direction, float velocity, float spin)
	{
		puck.setX(x);
		puck.setY(y);
		puck.setDirection(direction);
		puck.setVelocity(velocity);
		puck.setSpin(spin);

		puck.show();
	}

	public void setScore(int player1, int player2)
	{
		this.player1.setScore(player1);
		this.player2.setScore(player2);
	}

	public void setOpponentName(String name)
	{
		opponent.setName(name);
		if(panel != null)
		{
			doStartAnimation();
		} else {
			call.ready();
		}
	}

	public void setPlayerNumber(int playerNumber)
	{
		if(playerNumber == 1)
		{
			player1 = me = new HumanPlayer(call, assets, this, 1);
			if(panel != null)
				panel.addMouseMotionListener((HumanPlayer)me);
			player2 = opponent = new Player(assets, this, 2);
			player1.setName("Player 1");

		} else if(playerNumber == 2){
			player2 = me = new HumanPlayer(call, assets, this, 1);
			if(panel != null)
				panel.addMouseMotionListener((HumanPlayer)me);
			player1 = opponent = new Player(assets, this, 2);
			player2.setName("Player 2");
		} else {
			Log.e("INVALID PLAYER NUMBER: " + playerNumber);
			shutdown();
		}

		/* Load the paddle images */
		try
		{
			player1.getPaddle().loadImages();
			player2.getPaddle().loadImages();
		}catch(Exception e)
		{
			this.shutdown();
			return;
		}

		/* Set the positions of the paddles */
		player1.getPaddle().setCenter(board.getWidth() / 4, 
				board.getHeight() / 2);
		player2.getPaddle().setCenter((board.getWidth() * 3) / 4, 
				board.getHeight() / 2);

		if(panel != null)
		{
			/* Add the paddles to the board */
			panel.addRenderable(player1.getPaddle());
			panel.addCollidable(player1.getPaddle());
			panel.addRenderable(player2.getPaddle());
			panel.addCollidable(player2.getPaddle());
		}

		/* Set the name of the player */
		call.setName(me.getName());
	}

	public void hidePuck()
	{
		puck.hide();
	}

	public void doStartAnimation()
	{
		Runnable run = new Runnable()
		{
			public void run()
			{
				for(int x = 3;x > 0;x --)
				{
					currentPrompt.hide();
					currentPrompt.clearBackground();
					currentPrompt.setText("" + x);
					currentPrompt.setFontSize(64);
					currentPrompt.setCenter(panel.getWidth() / 2, 
							panel.getHeight() / 2);

					try {Thread.sleep(1000);} catch(Exception e){}
				}

				panel.removeGPRenderable(currentPrompt);

				/* Ready to play */
				call.ready();
			}
		};
		new Thread(run).start();

	}

	public void shutdown()
	{
		try { rpc.shutdown(); } catch(Exception e) {}

		rpc = null;
		call = null;

		window.hide();
		window.dispose();
	}

	public RenderPanel getPanel()
	{
		return panel;
	}

	private Player me;
	private Player opponent;

	private RenderWindow window;
	private RenderPanel panel;

	private PongRPCCaller call;
	private RPCManager rpc;

	private TextView currentPrompt;

	public static void main(String args[]) throws IOException
	{
		ClientGame game = new ClientGame();
		game.setupWindow();

		if(!game.connect())
		{
			System.out.println("Could not connect to server.");
			game.shutdown();
			return;
		}

		ClientGame game2 = new ClientGame();
		//game2.setupWindow();

		if(!game2.connect())
		{
			System.out.println("Could not connect to server.");
			game.shutdown();
			return;
		}


	}

	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 8081;
}
