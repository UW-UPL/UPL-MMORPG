package com.upl.mmorpg.lib.map;

import com.upl.mmorpg.game.item.Item;

public class GenerateMapSquare extends MapSquare
{
	/**
	 * Create a new GenerateMapSquare.
	 * @param row The row of the square.
	 * @param col The column of the square.
	 * @param image_name The image name of the background.
	 * @param overlay_name The image name of the overlay.
	 * @param destroyed_overlay_name The image name of the overlay 
	 * when this square is destroyed.
	 * @param item The item this square generates.
	 * @param regenerate The generation rate of this square (in ticks).
	 */
	public GenerateMapSquare(int row, int col, String image_name, 
			String overlay_name, String destroyed_overlay_name,
			Item item, int regenerate) 
	{
		super(row, col, image_name, overlay_name, destroyed_overlay_name);
		ticks = 0;
		this.regenerate = regenerate;
		this.generate = item;
		this.ready = true;
	}
	

	
	private Item generate; /**< The item to generate */
	private int regenerate; /**< The regenerate rate */
	private int ticks; /**< The amount of ticks that have passed */
	private boolean ready; /**< Whether or not we are ready to generate a new item */
	
	private static final long serialVersionUID = 8935231798534127004L;
}
