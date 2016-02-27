package com.upl.mmorpg.lib;

import com.upl.mmorpg.lib.libnet.Server;

public class TestMain 
{
	public static void main(String args[])
	{
		Server s = new Server(8080);
		s.shutdown();
	}
}
