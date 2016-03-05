package com.upl.mmorpg.lib.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class TextView extends Renderable
{
	public TextView(String text, Font font, Color color, int x, int y)
	{
		super();
		this.text = text;
		this.font = font;
		this.color = color;
		this.locX = x;
		this.locY = y;
		
		setFontSize(font.getSize());
	}
	
	public TextView(String text, Font font, int x, int y)
	{
		this(text, font, Color.black, x, y);
	}
	
	public TextView(String text, Color color, int x, int y)
	{
		this(text, DEFAULT_FONT, color, x, y);
	}
	
	public TextView(String text, int x, int y)
	{
		this(text, DEFAULT_FONT, Color.black, x, y);
	}
	
	public TextView(String text)
	{
		this(text, DEFAULT_FONT, Color.black, 0, 0);
	}
	
	private void updateProperties()
	{
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);     
		this.width = (int)(font.getStringBounds(text, frc).getWidth());
		this.height = (int)(font.getStringBounds(text, frc).getHeight());
		this.descent = (int)font.getLineMetrics(text, frc).getDescent();
	}
	
	public void setText(String text)
	{
		this.text = text;
		updateProperties();
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setFontSize(int sz)
	{
		font = new Font(font.getName(), font.getStyle(), sz);
		updateProperties();
	}
	
	public void setFontStyle(int style)
	{
		font = new Font(font.getName(), style, font.getSize());
		updateProperties();
	}
	
	public void setFontType(String name)
	{
		font = new Font(name, font.getStyle(), font.getSize());
		updateProperties();
	}
	
	public void setBackground(Color color)
	{
		this.backgroundColor = color;
		hasBackground = true;
		
		if(color == null)
			hasBackground = false;
		updateProperties();
	}
	
	public void clearBackground()
	{
		backgroundColor = null;
		hasBackground = false;
	}
	
	@Override
	public void render(Graphics2D g) 
	{
		if(hasBackground)
		{
			g.setColor(backgroundColor);
			g.fillRect((int)locX, (int)locY, (int)width, (int)height);
		}
		
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, (float)locX, (float)(locY + height - descent));
	}

	@Override
	public String getRenderName() 
	{
		return "TextView: " + text;
	}
	
	@Override
	public String toString() 
	{
		return "TextView: " + text;
	}

	private Color color;
	private Font font;
	private String text;
	
	private int descent;
	
	private boolean hasBackground;
	private Color backgroundColor;
	
	private static final Font DEFAULT_FONT = new Font("Times New Roman", Font.PLAIN, 12);
}
