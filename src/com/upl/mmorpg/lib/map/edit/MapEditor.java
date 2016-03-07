package com.upl.mmorpg.lib.map.edit;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;

public class MapEditor implements ActionListener, MouseMotionListener, MouseListener, WindowListener
{
	public MapEditor() throws IOException
	{
		assets = new AssetManager();
		toolButtons = new LinkedList<JToggleButton>();
		tileTools = new LinkedList<TileTool>();

		bar = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		bar.add(file_menu);
		
		new_file = new JMenuItem("New");
		file_menu.add(new_file);
		new_file.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String size = JOptionPane.showInputDialog("Map size: ");
				
				int sz = -1;
				try
				{
					sz = Integer.parseInt(size);
				} catch(Exception ex)
				{
					JOptionPane.showMessageDialog(window, "Invalid number!");
					return;
				}
				
				map.unload();
				map.createNewMap(sz, sz);
			}
		});
		
		open = new JMenuItem("Open");
		file_menu.add(open);
		open.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showOpenDialog(window);
				if(option == JFileChooser.APPROVE_OPTION)
				{
					map.unload();
					render.removeAllBPRenderable();
					try 
					{
						String path = chooser.getSelectedFile().getAbsolutePath();
						if(!path.endsWith(".mmomap"))
							path = path + ".mmomap";
						map.load(path, assets, TILE_SIZE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(window, "File does not exist or is currupted!");
						map.unload();
					}
				}
			}
		});
		save = new JMenuItem("Save");
		file_menu.add(save);
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showSaveDialog(window);
				if(option == JFileChooser.APPROVE_OPTION)
				{
					try 
					{
						String path = chooser.getSelectedFile().getAbsolutePath();
						if(!path.endsWith(".mmomap"))
							path = path + ".mmomap";
						map.export(path, assets);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(window, "Permission Denied");
						map.unload();
					}
				}
			}
		});
		
		window = new JFrame("UPL-MMORPG Map Editor");
		window.setJMenuBar(bar);
		window.addWindowListener(this);
		render = new RenderPanel(true, true);
		map = new EditableGrid2DMap(render);
		
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
		
		new TileTool("assets/images/tiles/grass1.png");
		
		addLabel("    Overlay Tools    ");
		new OverlayTool("assets/images/tiles/overlays/fence_l.png");
		new OverlayTool("assets/images/tiles/overlays/fence_ld.png");
		new OverlayTool("assets/images/tiles/overlays/fence_lu.png");
		new OverlayTool("assets/images/tiles/overlays/fence_lud.png");
		new OverlayTool("assets/images/tiles/overlays/fence_r.png");
		new OverlayTool("assets/images/tiles/overlays/fence_rd.png");
		new OverlayTool("assets/images/tiles/overlays/fence_ru.png");
		new OverlayTool("assets/images/tiles/overlays/fence_rud.png");
		new OverlayTool("assets/images/tiles/overlays/fence_rl.png");
		
		addLabel("    Tile Properties    ");
		
		new PassThroughTool("assets/images/editor/passThroughTool.png", true);
		new PassThroughTool("assets/images/editor/impassibleTool.png", false);
		new DestructibleTool("assets/images/editor/destructibleTool.png", true);
		new DestructibleTool("assets/images/editor/indestructibleTool.png", false);
		
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
		
		if(!b.isSelected()) b.setSelected(true);
		
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
				for(int x = 0;x < brush;x++)
					for(int y = 0;y < brush;y++)
						map.deleteSquare(row + x - brush_sub,
								col + y - brush_sub);
				
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
				for(int x = 0;x < brush;x++)
					for(int y = 0;y < brush;y++)
						map.deleteSquare(row + x - brush_sub,
								col + y - brush_sub);
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
	
	@Override public void windowClosing(WindowEvent arg0) 
	{
		System.exit(0);
	}
	
	@Override public void mouseClicked(MouseEvent arg0) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseMoved(MouseEvent arg0) {}
	@Override public void windowActivated(WindowEvent arg0) {}
	@Override public void windowClosed(WindowEvent arg0) {}
	@Override public void windowDeactivated(WindowEvent arg0) {}
	@Override public void windowDeiconified(WindowEvent arg0) {}
	@Override public void windowIconified(WindowEvent arg0) {}
	@Override public void windowOpened(WindowEvent arg0) {}

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
	private EditableGrid2DMap map;

	private JPanel parentPanel;
	private RenderPanel render;

	private JPanel toolPanel;
	private JToggleButton moveTool;
	private JToggleButton eraseTool;
	private JTextField sz_field;
	
	private boolean dragging;
	private double lastDragX;
	private double lastDragY;
	private int brush_size;

	private Tools currTool;
	private TileTool currTileTool;

	private AssetManager assets;

	private LinkedList<JToggleButton> toolButtons;
	private LinkedList<TileTool> tileTools;
	
	private JMenuBar bar;
	private JMenuItem new_file;
	private JMenuItem save;
	private JMenuItem open;
	
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
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			boolean set = false;
			
			if((square = map.getSquare(row, col)) == null)
			{
				square = new EditableMapSquare(
						col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE,
						assets, texture_file, null, null);
				set = true;
			} else {
				square.setImage(texture_file);
			}
			
			try 
			{
				/* reload images */
				square.loadImages();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(set) map.setSquare(row, col, square);
		}
		
		private JToggleButton getButton()
		{
			return button;
		}
		
		protected JToggleButton button;
		protected String texture_file;
	}
	
	private class OverlayTool extends TileTool
	{
		public OverlayTool(String texture_file) throws IOException 
		{
			super(texture_file);
		}
		
		public void mouseDragged(int row, int col)
		{
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			boolean set = false;
			
			if((square = map.getSquare(row, col)) == null)
			{
				square = new EditableMapSquare(
						col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE,
						assets, null, null, null);
				set = true;
			} else {
				square.setOverlay(texture_file);
			}
			
			try 
			{
				/* reload images */
				square.loadImages();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(set) map.setSquare(row, col, square);
		}
	}
	
	private class PassThroughTool extends TileTool
	{
		public PassThroughTool(String texture_file, boolean passThrough) 
				throws IOException 
			{
			super(texture_file);
			this.passThrough = passThrough;
		}
		
		public void mouseDragged(int row, int col)
		{
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			
			if((square = map.getSquare(row, col)) == null)
			{
				return;
			} else {
				square.setPassThrough(passThrough);
			}
		}

		private boolean passThrough;
	}
	
	private class DestructibleTool extends TileTool
	{
		public DestructibleTool(String texture_file, boolean destructible) 
				throws IOException 
			{
			super(texture_file);
			this.destructible = destructible;
		}
		
		public void mouseDragged(int row, int col)
		{
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			
			if((square = map.getSquare(row, col)) == null)
			{
				return;
			} else {
				square.setDestructible(destructible);
			}
		}

		private boolean destructible;
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
