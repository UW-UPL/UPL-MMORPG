package com.upl.mmorpg.game.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.upl.mmorpg.game.server.ServerGame;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;

public class ClientLogin 
{
	public ClientLogin()
	{
		assets = new AssetManager();
		try 
		{
			background = assets.loadImage("assets/images/login_screen.png");
		} catch (IOException e) 
		{
			Log.wtf("Couldn't load background image!", e);
			return;
		}
		frame = new JFrame("UPL MMORPG");

		mainPanel = new MainPanel();
		mainPanel.setLayout(new GridBagLayout());

		Dimension mainPanelDimen = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
		mainPanel.setPreferredSize(mainPanelDimen);
		mainPanel.setMaximumSize(mainPanelDimen);
		mainPanel.setMinimumSize(mainPanelDimen);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());
		mainPanel.add(loginPanel, new GridBagConstraints());

		JPanel welcomePanel = new JPanel();
		welcomeL = new JLabel("Welcome to UPL-MMORPG!");
		welcomePanel.add(welcomeL);
		loginPanel.add(welcomePanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		JPanel usernamePanel = new JPanel();
		usernameL = new JLabel("Username:");
		usernameTF = new JTextField(16);
		usernamePanel.add(usernameL);
		usernamePanel.add(usernameTF);
		contentPanel.add(usernamePanel, BorderLayout.NORTH);

		JPanel passwordPanel = new JPanel();
		passwordL = new JLabel("Password:");
		passwordTF = new JPasswordField(16);
		passwordPanel.add(passwordL);
		passwordPanel.add(passwordTF);
		contentPanel.add(passwordPanel, BorderLayout.SOUTH);

		loginPanel.add(contentPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		loginB = new JButton("Login");
		cancelB = new JButton("Cancel");
		setListeners();
		buttonPanel.add(loginB);
		buttonPanel.add(cancelB);
		loginPanel.add(buttonPanel, BorderLayout.SOUTH);

		frame.getContentPane().add(mainPanel);

		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		connect();
	}

	private void connect()
	{
		Runnable run = new Runnable()
		{
			public void run()
			{
				try 
				{
					rpc = new RPCManager("localhost", 8080, null);
				} catch (IOException e) 
				{
					Log.wtf("Couldn't connect to server!", e);
					System.exit(1);
				}

				login = new LoginClientCaller(rpc);
				try
				{
					if(!login.hello())
					{
						System.out.println("Client server may have incompatable versions!");
						System.exit(1);
					}
				} catch(Exception e)
				{
					System.out.println("Client and server out of sync!");
					System.exit(1);
				}
			}
		};
		new Thread(run).start();
	}
	
	public void loginSuccess()
	{
		System.out.println("Login success!");
		
		frame.dispose();
		new ClientGame(new AssetManager(), rpc);
	}
	
	public void loginFailure()
	{
		System.out.println("Login failure!");
		welcomeL.setText("Username/password combo failed!");
		welcomeL.repaint();
	}
	
	public void login(final String username, final String password)
	{
		Runnable run = new Runnable()
		{
			public void run()
			{
				final boolean success = login.login(username, password.getBytes());
					Runnable run2 = new Runnable()
					{
						public void run()
						{
							if(success) loginSuccess();
							else loginFailure();
						}
					};
					SwingUtilities.invokeLater(run2);
			}
		};
		new Thread(run).start();
	}

	private void setListeners()
	{
		loginB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				login(usernameTF.getText(), passwordTF.getText());
			}
		});

		cancelB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
				System.exit(0);
			}
		});
	}

	@SuppressWarnings("serial")
	public class MainPanel extends JPanel
	{
		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(background, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
		}
	}

	private JFrame frame;
	private MainPanel mainPanel;

	private AssetManager assets;
	private BufferedImage background;

	private JLabel welcomeL;
	private JLabel usernameL;
	private JLabel passwordL;
	private JTextField usernameTF;
	private JTextField passwordTF;
	private JButton loginB;
	private JButton cancelB;

	private RPCManager rpc;
	private LoginClientCaller login;

	private static final int PANEL_WIDTH = 800;
	private static final int PANEL_HEIGHT = 600;

	public static void main(String args[])
	{
		/* Start the server */
		// ServerGame.main(args);

		/* Open the Client Window*/
		ClientLogin login = new ClientLogin();
		// Uncomment the lines below for automatic login (testing only)
		try { Thread.sleep(2000);} catch (InterruptedException e) {}
		login.login("jdetter", "password");
	}
}
