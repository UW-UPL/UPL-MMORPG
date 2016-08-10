package com.upl.mmorpg.lib.gui;

import java.io.IOException;

import javax.swing.JFrame;

public class RenderWindow 
{
	public RenderWindow()
	{
		frame = new JFrame("UPL-MMORPG");
		panel = new RenderPanel(true, true, false);
		
		frame.getContentPane().add(panel);
		
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
	
	public RenderPanel getPanel()
	{
		return panel;
	}
	
	public void show(AssetManager assets) throws IOException
	{
		panel.loadAllImages(assets);
		panel.startRender();
		frame.setVisible(true);
	}
	
	public void hide()
	{
		panel.stopRender();
		frame.setVisible(false);
	}
	
	public void dispose()
	{
		panel.stopRender();
		frame.dispose();
	}
	
	private JFrame frame;
	private RenderPanel panel;
}
