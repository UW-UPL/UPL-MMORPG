package com.upl.examplegame.pong;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.lib.collision.CollideCircle;
import com.upl.mmorpg.lib.gui.AssetManager;
import com.upl.mmorpg.lib.gui.Renderable;

public class Paddle extends Renderable
{
	public Paddle(AssetManager assets, Board board, Player player, int player_num)
	{
		super();
		this.assets = assets;
		this.board = board;
		this.player = player;
		this.player_num = player_num;
		this.paddle_image = -1;

		collision = new CollideCircle(0, 0, 0);
		collision_shapes.add(collision);
	}

	@Override
	public void setX(double x)
	{
		double oldX = locX;
		super.setX(x);
		collision.setCenter(locX, locY);
		
		if(collision_manager != null)
		{
			if(!collision_manager.isBounded(collision)
					|| collision_manager.isColliding(collision))
			{
				/* Undo the change */
				this.locX = oldX;
				collision.setCenter(locX, locY);
			}
		}
	}

	@Override
	public void setY(double y)
	{
		double oldY = locY;
		super.setY(y);
		collision.setCenter(locX, locY);
		
		if(collision_manager != null)
		{
			if(!collision_manager.isBounded(collision)
					|| collision_manager.isColliding(collision))
			{
				/* Undo the change */
				this.locY = oldY;
				collision.setCenter(locX, locY);
			}
		}
	}

	@Override
	public Point getCenter()
	{
		Point p = new Point();
		p.setLocation(locX, locY);
		return p;
	}

	@Override
	public void setCenter(double x, double y)
	{
		double oldX = locX;
		double oldY = locY;

		locX = x;
		locY = y;
		collision.setCenter(x, y);

		if(collision_manager != null)
		{
			if(!collision_manager.isBounded(collision)
					|| collision_manager.isColliding(collision))
			{
				/* Undo the change */
				this.locX = oldX;
				this.locY = oldY;
			}
		}
	}

	@Override
	public void render(Graphics2D g) 
	{
		if(paddle_image >= 0)
		{
			double r = this.width / 2;
			BufferedImage img = assets.getImage(paddle_image);
			g.drawImage(img, (int)(locX - r), (int)(locY - r), 
					(int)(r * 2), (int)(r * 2), null);
		}
	}

	@Override
	public void loadImages() throws IOException
	{
		paddle_image = assets.loadImage("assets/images/paddle.png");

		BufferedImage img = assets.getImage(paddle_image);
		this.width = img.getWidth();
		this.height = img.getHeight();
		collision.setRadius(this.width / 2);
	}

	@Override
	public String getRenderName() 
	{
		return player.getName() + "'s paddle";
	}

	private Player player;
	private int player_num;
	private Board board;

	private AssetManager assets;
	private int paddle_image;

	private CollideCircle collision;
}
