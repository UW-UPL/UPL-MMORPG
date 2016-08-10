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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.RenderPanel;

/**
 * Let's you create maps for the MMO.
 * 
 * @author John Detter <jdetter@wisc.edu>
 *
 */

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
				render.setView(0, 0);
			}
		});
		
		/* Setup the map open menu item. */
		open = new JMenuItem("Open");
		file_menu.add(open);
		open.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				/* Setup the file chooser */
				JFileChooser chooser = new JFileChooser();
				
				/* Wait for the user to select the file */
				int option = chooser.showOpenDialog(window);
				
				/* Was there a file chosen or was the dialog canceled? */
				if(option == JFileChooser.APPROVE_OPTION)
				{
					/* Unload the current map */
					map.unload();
					
					try 
					{
						String path = chooser.getSelectedFile().getAbsolutePath();
						
						/* Did the user not include the proper extension? */
						if(!path.endsWith(".mmomap"))
							path = path + ".mmomap";
						
						/* Load the map from the file */
						if(map.load(path, assets, TILE_SIZE))
						{
							/* Generate the square properties for this screen size */
							map.generateSquareProperties();
							/* Load all of the assets for this map */
							map.loadAllImages(assets);
						} else {
							/* Couldn't load the map file */
							JOptionPane.showMessageDialog(window, "Map format exception!");
						}
					} catch (IOException e1) {
						/* There was an issue with the actual file. */
						JOptionPane.showMessageDialog(window, "File does not exist or is currupted!");
						map.unload();
					}
				}
			}
		});
		
		/* Setup the save menu item */
		save = new JMenuItem("Save");
		file_menu.add(save);
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				/* Open the file chooser */
				JFileChooser chooser = new JFileChooser();
				/* Wait for the user to select a file */
				int option = chooser.showSaveDialog(window);
				
				/* Did the user complete the dialog or cancel it? */
				if(option == JFileChooser.APPROVE_OPTION)
				{
					try 
					{
						String path = chooser.getSelectedFile().getAbsolutePath();
						/* Does the file have the proper extension? */
						if(!path.endsWith(".mmomap"))
							path = path + ".mmomap";
						
						/* Save the file */
						map.export(path, assets);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(window, "Permission Denied");
						map.unload();
					}
				}
			}
		});
		
		/* Setup the window and set the menu bar */
		window = new JFrame("UPL-MMORPG Map Editor");
		window.setJMenuBar(bar);
		window.addWindowListener(this);
		
		/* Initialize the render panel */
		render = new RenderPanel(true, true, false);
		/* Create an empty map (not loaded) */
		map = new EditableGrid2DMap(render, TILE_SIZE);
		render.addBPRenderable(map);
		
		/* This holds the panel that renders the map and the panel that renders the tools */
		parentPanel = new JPanel();

		/* Setup the tool panel */
		toolPanel = new JPanel();
		toolPanel.setBackground(Color.gray);
		toolPanel.setMaximumSize(new Dimension(TOOLS_PANEL_WIDTH, TOOLS_PANEL_HEIGHT));
		toolPanel.setMinimumSize(new Dimension(TOOLS_PANEL_WIDTH, TOOLS_PANEL_HEIGHT));
		toolPanel.setPreferredSize(new Dimension(TOOLS_PANEL_WIDTH, TOOLS_PANEL_HEIGHT));
		JScrollPane scroll = new JScrollPane(toolPanel);
		scroll.setMaximumSize(new Dimension(TOOLS_PANEL_WIDTH + 25, 600));
		scroll.setMinimumSize(new Dimension(TOOLS_PANEL_WIDTH + 25, 600));
		scroll.setPreferredSize(new Dimension(TOOLS_PANEL_WIDTH + 25, 600));
		scroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		addLabel("Control Tools");
		JPanel controlPanel = new JPanel();
		addPanel(80, controlPanel);
		moveTool = addTool("assets/images/editor/move_tool.png", controlPanel);
		eraseTool = addTool("assets/images/editor/erase_tool.png", controlPanel);

		addLabel("    Tile Tools    ");
		JPanel tileToolsPanel = new JPanel();
		addPanel(50, tileToolsPanel);
		new TileTool("assets/images/tiles/grass1.png", tileToolsPanel);
		new TileTool("assets/images/tiles/desert1.png", tileToolsPanel);
		new TileTool("assets/images/tiles/snow1.png", tileToolsPanel);
		
		addLabel("    Overlay Tools    ");
		JPanel overlayToolsPanel = new JPanel();
		addPanel(225, overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_l.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_ld.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_lu.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_lud.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_r.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_rd.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_ru.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_rud.png", overlayToolsPanel);
		new OverlayTool("assets/images/tiles/overlays/fence_rl.png", overlayToolsPanel);
		
		new ExpandingOverlayTool("assets/images/tiles/overlays/tree_left.png",
				new String[][]{
				{"assets/images/tiles/overlays/tree_left_ul.png", 
					"assets/images/tiles/overlays/tree_left_ur.png"}, 
				{"assets/images/tiles/overlays/tree_left_dl.png", 
						"assets/images/tiles/overlays/tree_left_dr.png"}
				}, overlayToolsPanel);
		new ExpandingOverlayTool("assets/images/tiles/overlays/tree_mid.png",
				new String[][]{
				{"assets/images/tiles/overlays/tree_mid_ul.png", 
					"assets/images/tiles/overlays/tree_mid_ur.png"}, 
				{"assets/images/tiles/overlays/tree_mid_dl.png", 
						"assets/images/tiles/overlays/tree_mid_dr.png"}
				}, overlayToolsPanel);
		new ExpandingOverlayTool("assets/images/tiles/overlays/tree_right.png",
				new String[][]{
				{"assets/images/tiles/overlays/tree_right_ul.png", 
					"assets/images/tiles/overlays/tree_right_ur.png"}, 
				{"assets/images/tiles/overlays/tree_right_dl.png", 
						"assets/images/tiles/overlays/tree_right_dr.png"}
				}, overlayToolsPanel);
		
		addLabel("    Tile Properties    ");
		JPanel tilePropertiesPanel = new JPanel();
		addPanel(80, tilePropertiesPanel);
		new PassThroughTool("assets/images/editor/passThroughTool.png", true, tilePropertiesPanel);
		new PassThroughTool("assets/images/editor/impassibleTool.png", false, tilePropertiesPanel);
		new DestructibleTool("assets/images/editor/destructibleTool.png", true, tilePropertiesPanel);
		new DestructibleTool("assets/images/editor/indestructibleTool.png", false, tilePropertiesPanel);
		new LandingTool("assets/images/editor/landingTool.png", tilePropertiesPanel);
		new LinkTool("assets/images/editor/linkTool.png", tilePropertiesPanel);
		
		addLabel("Brush Size: ");
		JPanel brushPanel = new JPanel();
		addPanel(50, brushPanel);
		sz_field = new JTextField(2);
		sz_field.setFont(SMALL_FONT);
		sz_field.setText("1");
		brushPanel.add(sz_field);
		
		/**
		 * Focus listener for the brush size field. This just updates the brush
		 * size when the text field loses focus.
		 */
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
		setBrushSize(1);
		
		parentPanel.add(render);
		parentPanel.add(scroll);
		
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
		
		/* Start rendering the map */
		render.startRender();
	}
	
	public void addPanel(int height, JPanel panel)
	{
		panel.setPreferredSize(new Dimension(TOOLS_PANEL_WIDTH, height));
		panel.setMaximumSize(new Dimension(TOOLS_PANEL_WIDTH, height));
		panel.setMinimumSize(new Dimension(TOOLS_PANEL_WIDTH, height));
		panel.setBackground(Color.gray);
		toolPanel.add(panel);
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
					if(currTileTool.isBrushable())
					{
						for(int x = 0;x < brush;x++)
							for(int y = 0;y < brush;y++)
								currTileTool.mouseDragged(row + x - brush_sub,
										col + y - brush_sub);
					} else currTileTool.mouseDragged(row, col);
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
				if(currTileTool.isBrushable())
				{
					for(int x = 0;x < brush;x++)
						for(int y = 0;y < brush;y++)
							currTileTool.mouseDragged(row + x - brush_sub,
									col + y - brush_sub);
				} else currTileTool.mouseDragged(row, col);
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

	private JToggleButton addTool(String path, JPanel panel) throws IOException
	{
		BufferedImage img = assets.loadImage(path);
		
		JToggleButton button = new JToggleButton(new ImageIcon(img));
		button.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		button.setMinimumSize(new Dimension(img.getWidth(), img.getHeight()));
		button.setMaximumSize(new Dimension(img.getWidth(), img.getHeight()));
		button.addActionListener(this);
		panel.add(button);
		toolButtons.add(button);

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
	private static final int TOOLS_PANEL_WIDTH = 200;
	private static final int TOOLS_PANEL_HEIGHT = 800;
	private static final Font LARGE_FONT = new Font("Times New Roman", Font.PLAIN, 30);
	private static final Font SMALL_FONT = new Font("Times New Roman", Font.PLAIN, 18);

	private enum Tools
	{
		NONE, MOVE, ERASE, TILE
	}
	
	private class TileTool
	{
		public TileTool(String texture_file, JPanel panel) throws IOException
		{
			button = addTool(texture_file, panel);
			tileTools.add(this);
			brushable = true;
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
						texture_file, null, null);
				set = true;
			} else {
				square.setImage(texture_file);
			}
			
			try 
			{
				/* reload images */
				square.loadImages(assets);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(set) map.setSquare(row, col, square);
		}
		
		private JToggleButton getButton()
		{
			return button;
		}
		
		public boolean isBrushable()
		{
			return brushable;
		}
		
		protected JToggleButton button;
		protected String texture_file;
		protected boolean brushable;
	}
	
	private class OverlayTool extends TileTool
	{
		public OverlayTool(String texture_file, JPanel panel) throws IOException 
		{
			super(texture_file, panel);
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
						null, null, null);
				set = true;
			} else {
				square.setOverlay(texture_file);
			}
			
			try 
			{
				/* reload images */
				square.loadImages(assets);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(set) map.setSquare(row, col, square);
		}
	}
	
	private class ExpandingOverlayTool extends OverlayTool
	{
		public ExpandingOverlayTool(String complete, 
				String texture_files[][], JPanel panel) throws IOException 
		{
			super(complete, panel);
			brushable = false;
			this.texture_files = texture_files;
		}
		
		public void mouseDragged(int row_input, int col_input)
		{
			if(row_input < 0 || col_input < 0) return;

			for(int r = 0;r < texture_files.length;r++)
			{
				for(int c = 0;c < texture_files[0].length;c++)
				{
					EditableMapSquare square = null;
					boolean set = false;
					
					int row = row_input + r;
					int col = col_input + c;
					
					if((square = map.getSquare(row, col)) == null)
					{
						square = new EditableMapSquare(
								col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE,
								null, null, null);
						set = true;
					}
					
					square.setOverlay(texture_files[r][c]);
					
					try 
					{
						/* reload images */
						square.loadImages(assets);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(set) map.setSquare(row, col, square);
				}
			}
		}
		
		private String texture_files[][];
	}
	
	private class PassThroughTool extends TileTool
	{
		public PassThroughTool(String texture_file, boolean passThrough, JPanel panel) 
				throws IOException 
			{
			super(texture_file, panel);
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
		public DestructibleTool(String texture_file, boolean destructible, JPanel panel) 
				throws IOException 
		{
			super(texture_file, panel);
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
	
	private class LinkTool extends TileTool
	{
		public LinkTool(String texture_file, JPanel panel) 
				throws IOException 
		{
			super(texture_file, panel);
			brushable = false;
		}
		
		public void mouseDragged(int row, int col)
		{
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			
			if((square = map.getSquare(row, col)) == null)
			{
				return;
			} else {
				try
				{
					JFileChooser chooser = new JFileChooser();
					int option = chooser.showOpenDialog(window);
					if(option == JFileChooser.APPROVE_OPTION)
					{
						String path = chooser.getSelectedFile().getAbsolutePath();
						
						int landings[][] = EditableGrid2DMap.getAllLandings(path, assets);
						String options[] = new String[landings.length];
						for(int x = 0;x < landings.length;x++)
							options[x] = "Row: " + landings[x][0] + "  Col: " + landings[x][1];
						
						String result = (String)JOptionPane.showInputDialog(window, 
								"Which landing do you want to use?",
								"Landing selection", JOptionPane.PLAIN_MESSAGE, null,
								options, options[0]);
						
						System.out.println("Chose: " + result);
						boolean found = false;
						int x;
						for(x = 0;x < options.length;x++)
						{
							if(options[x].equalsIgnoreCase(result))
							{
								found = true;
								break;
							}
						}
						
						if(found)
							square.setMapLink(path, landings[x][0], landings[x][1]);
						else throw new Exception();
					}
				} catch(Exception e){JOptionPane.showMessageDialog(window, "An Error occurred.");}
			}
		}
	}
	
	private class LandingTool extends TileTool
	{
		public LandingTool(String texture_file, JPanel panel) throws IOException 
		{
			super(texture_file, panel);
			brushable = false;
		}
		
		public void mouseDragged(int row, int col)
		{
			if(row < 0 || col < 0) return;
			
			EditableMapSquare square = null;
			
			if((square = map.getSquare(row, col)) == null)
			{
				return;
			} else {
				square.setLanding(true);
			}
		}
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
