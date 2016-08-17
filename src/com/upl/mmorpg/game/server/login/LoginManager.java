package com.upl.mmorpg.game.server.login;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.map.GameMap;

public class LoginManager implements LoginInterface
{
	public LoginManager(Game g)
	{
		this.g = g;
	}
	
	public boolean login(String username, byte[] password)
	{
		Log.vnetln(username + " has logged in.");
		
		g.createGoblin(12, 12, GameMap.EXAMPLE1);
		
		return true;
	}
	
    public boolean hello()
    {
    	return true;
    }
    
    private Game g;
}
