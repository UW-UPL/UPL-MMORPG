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
		return Math.hypot(x2 - x1, y2 - 1);
	}
}
