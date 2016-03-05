package com.upl.mmorpg.lib.gui;

public final class RenderMath 
{
	public static final int calculateVSYNC(int fps)
	{
		return (1000 / fps) + 1;
	}
	
	public static final double pointDistance(double x1, double y1,
				double x2, double y2)
	{
		double a = x2 - x1;
		double b = y2 - y1;
		
		return Math.sqrt(a * a + b * b);
	}
}
