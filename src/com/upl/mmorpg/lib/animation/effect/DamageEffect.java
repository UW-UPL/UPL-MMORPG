package com.upl.mmorpg.lib.animation.effect;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.gui.AssetManager;

public class DamageEffect extends CharacterEffect 
{
	public DamageEffect(MMOCharacter character, AssetManager assets)
	{
		super(character, assets);
		centerX = character.getCenterX();
		centerY = character.getCenterY();
		damage_amount = 0;
		damage_str = null;
		splat = null;
		visible_time = 2.0d;
	}

	public void setAmount(int amount)
	{
		this.damage_amount = amount;
		this.damage_str = "" + this.damage_amount;
	}

	public void loadAssets() throws IOException
	{
		splat = assets.loadImage("assets/models/effects/blood_splat.png");
		width = splat.getWidth();
		height = splat.getHeight();
	}

	@Override
	public void render(Graphics2D g) 
	{
		if(splat != null)
		{
			g.drawImage(splat, 
					(int)(centerX - width / 2), 
					(int)(centerY - height / 2), 
					(int)width, (int)height, null);
		}

		if(damage_str != null)
		{
			int width = g.getFontMetrics().stringWidth(damage_str);
			g.setFont(damage_font);
			g.setColor(Color.WHITE);
			g.drawString(damage_str, (int)(centerX - (width / 2)), 
					(int)(centerY + damage_font_sz / 4));
		}
	}

	public static void prefetchAssets(AssetManager assets) throws IOException
	{
		assets.loadImage("assets/models/effects/blood_splat.png");
	}

	@Override
	public boolean animation(double seconds) 
	{
		visible_time -= seconds;
		if(visible_time < 0)
			return true;
		
		centerY -= (seconds += FLOAT_SPEED);
		
		return false;
	}

	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private double visible_time;
	private int damage_amount;
	private String damage_str;

	private transient BufferedImage splat;

	private static final double FLOAT_SPEED = 0.25d;

	private static final int damage_font_sz = 20;
	private static final Font damage_font = new Font("Times New Roman", 
			Font.PLAIN, damage_font_sz);
	
	private static final long serialVersionUID = 635830854469552966L;
}
