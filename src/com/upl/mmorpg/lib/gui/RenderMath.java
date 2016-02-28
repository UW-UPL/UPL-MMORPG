package com.upl.mmorpg.lib.gui;

public class RenderMath 
{
	public static final int calculateVSYNC(int fps)
	{
		return (1000 / fps) + 1;
	}
}
