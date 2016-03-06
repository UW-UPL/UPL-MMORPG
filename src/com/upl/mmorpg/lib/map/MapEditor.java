package com.upl.mmorpg.lib.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;

public class MapEditor implements ActionListener, MouseMotionListener, MouseListener
{
	public MapEditor() throws IOException
	{
		assets = new AssetManager();
		toolButtons = new LinkedList<JToggleButton>();
		tileTools = new LinkedList<TileTool>();

		window = new JFrame("UPL-MMORPG Map Editor");
		render = new RenderPanel(true, true);

		map = new Grid2DMap(render);
		map.createNewMap(100, 100);
		
		parentPanel = new JPanel();

		toolPanel = new JPanel();
		toolPanel.setBackground(Color.gray);
		toolPanel.setMaximumSize(new Dimension(200, 600));
		toolPanel.setMinimumSize(new Dimension(200, 600));
		toolPanel.setPreferredSize(new Dimension(200, 600));

		addLabel("Control Tools");
		
		moveTool = addTool("assets/images/editor/move_tool.png");
		eraseTool = addTool("assets/images/editor/erase_tool.png");

		addLabel("    Tile Tools    ");
		
		grass1 = new TileTool("assets/images/tiles/grass1.png");
		fencel = new TileTool("assets/images/tiles/fence_l.png");
		fenceld = new TileTool("assets/images/tiles/fence_ld.png");
		fencelu = new TileTool("assets/images/tiles/fence_lu.png");
		fencelud = new TileTool("assets/images/tiles/fence_lud.png");
		fencer = new TileTool("assets/images/tiles/fence_r.png");
		fencerd = new TileTool("assets/images/tiles/fence_rd.png");
		fenceru = new TileTool("assets/images/tiles/fence_ru.png");
		fencerud = new TileTool("assets/images/tiles/fence_rud.png");
		fencerl = new TileTool("assets/images/tiles/fence_rl.png");
		
		addLabel("Brush Size: ");
		sz_field = new JTextField(2);
		sz_field.setFont(SMALL_FONT);
		sz_field.setText("1");
		setBrushSize(1);
		FocusListener sz_field_listen = new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent arg0) {}

			@Override
			public void focusLost(FocusEvent arg0) 
			{
				try
				{
					int sz = Integer.parseInt(sz_field.getText());
					if(sz < 0 || sz > 16)
						throw new RuntimeException("Brush too big!");
					setBrushSize(sz);
				}catch(Exception e)
				{
					sz_field.setText("1");
					setBrushSize(1);
				}
			}
			
		};
		sz_field.addFocusListener(sz_field_listen);
		toolPanel.add(sz_field);
		
		parentPanel.add(render);
		parentPanel.add(toolPanel);

		window.getContentPane().add(parentPanel);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
		
		render.addMouseListener(this);
		render.addMouseMotionListener(this);

		stopDragging();
		currTool = Tools.NONE;
		currTileTool = null;
		
		render.startRender();
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		JToggleButton b = (JToggleButton)event.getSource();
		untoggleAll(b);
		
		currTool = Tools.NONE;
		if(b == moveTool)
			currTool = Tools.MOVE;
		else if(b == eraseTool)
			currTool = Tools.ERASE;
		
		if(currTool == Tools.NONE)
		{
			Iterator<TileTool> it = tileTools.iterator();
			while(it.hasNext())
			{
				TileTool tool = it.next();
				if(tool.getButton() == b)
				{
					currTool = Tools.TILE;
					currTileTool = tool;
					break;
				}
			}
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent event) 
	{
		Point lastDrag = this.getLastDragLoc();
		double diffX = lastDrag.getX() - event.getX();
		double diffY = lastDrag.getY() - event.getY();
		
		int row = (int)(event.getY() + render.getViewY()) / TILE_SIZE;
		int col = (int)(event.getX() + render.getViewX()) / TILE_SIZE;
		
		int brush = getBrushSize();
		int brush_sub = brush / 2;
		
		switch(currTool)
		{
			case MOVE:
				if(dragging)
					render.moveView(diffX, diffY);
				break;
			case TILE:
				if(dragging && currTileTool != null)
					for(int x = 0;x < brush;x++)
						for(int y = 0;y < brush;y++)
							currTileTool.mouseDragged(row + x - brush_sub,
									col + y - brush_sub);
				break;
			case ERASE:
				map.deleteSquare(row, col);
				break;
			default:
				break;
		}
		
		/* Update drag */
		if(dragging)
			setDragging(event.getX(), event.getY());
	}
	
	@Override
	public void mousePressed(MouseEvent event) 
	{
		render.grabFocus();
		setDragging(event.getX(), event.getY());
		
		int row = (int)(event.getY() + render.getViewY()) / TILE_SIZE;
		int col = (int)(event.getX() + render.getViewX()) / TILE_SIZE;
		
		int brush = getBrushSize();
		int brush_sub = brush / 2;
		
		switch(currTool)
		{
			case TILE:
				for(int x = 0;x < brush;x++)
					for(int y = 0;y < brush;y++)
						currTileTool.mouseDragged(row + x - brush_sub,
								col + y - brush_sub);
				break;
			case ERASE:
				map.deleteSquare(row, col);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		stopDragging();
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		stopDragging();
	}
	
	@Override public void mouseClicked(MouseEvent arg0) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseMoved(MouseEvent arg0) {}

	private JToggleButton addTool(String path) throws IOException
	{
		BufferedImage img = assets.loadImage(path);
		
		JToggleButton button = new JToggleButton(new ImageIcon(img));
		button.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		button.setMinimumSize(new Dimension(img.getWidth(), img.getHeight()));
		button.setMaximumSize(new Dimension(img.getWidth(), img.getHeight()));
		button.addActionListener(this);
		toolButtons.add(button);
		toolPanel.add(button);

		return button;
	}
	
	private void addLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(LARGE_FONT);
		toolPanel.add(label);
	}

	private void untoggleAll(JToggleButton exception)
	{
		Iterator<JToggleButton> it = toolButtons.iterator();
		while(it.hasNext())
		{
			JToggleButton b = it.next();
			if(b == exception) continue;
			b.setSelected(false);
		}
	}


	
	private synchronized void stopDragging()
	{
		lastDragX = 0.0d;
		lastDragY = 0.0d;
		dragging = false;
	}
	
	private synchronized void setDragging(double x, double y)
	{
		lastDragX = x;
		lastDragY = y;
		dragging = true;
	}
	
	private synchronized Point getLastDragLoc()
	{
		return new Point((int)lastDragX, (int)lastDragY);
	}

	private synchronized int getBrushSize()
	{
		return brush_size;
	}
	
	public void setBrushSize(int sz)
	{
		brush_size = sz;
	}
	
	private JFrame window;
	private Grid2DMap map;

	private JPanel parentPanel;
	private RenderPanel render;

	private JPanel toolPanel;
	private JToggleButton moveTool;
	private JToggleButton eraseTool;
	private JTextField sz_field;
	
	/* Terrain tools */
	private TileTool grass1;
	
	/* Fence tools */
	private TileTool fencel;
	private TileTool fenceld;
	private TileTool fencelu;
	private TileTool fencelud;
	private TileTool fencer;
	private TileTool fencerd;
	private TileTool fenceru;
	private TileTool fencerud;
	private TileTool fencerl;
	
	private boolean dragging;
	private double lastDragX;
	private double lastDragY;
	private int brush_size;

	private Tools currTool;
	private TileTool currTileTool;

	private AssetManager assets;

	private LinkedList<JToggleButton> toolButtons;
	private LinkedList<TileTool> tileTools;
	
	private static final int TILE_SIZE = 32;
	private static final Font LARGE_FONT = new Font("Times New Roman", Font.PLAIN, 30);
	private static final Font SMALL_FONT = new Font("Times New Roman", Font.PLAIN, 18);

	private enum Tools
	{
		NONE, MOVE, ERASE, TILE
	}
	
	private class TileTool
	{
		public TileTool(String texture_file) throws IOException
		{
			button = addTool(texture_file);
			tileTools.add(this);
			this.texture_file = texture_file;
		}
		
		public void mouseDragged(int row, int col)
		{
			System.out.println("ROW: " + row + " COL: " + col);
			if(row < 0 || col < 0) return;
			
			MapSquare square = new MapSquare(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE,
					assets, texture_file);
			try {
				square.loadImages();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.setSquare(row, col, square);
		}
		
		private JToggleButton getButton()
		{
			return button;
		}
		
		private JToggleButton button;
		private String texture_file;
	}
	
	public static void main(String args[])
	{
		try {
			new MapEditor();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
