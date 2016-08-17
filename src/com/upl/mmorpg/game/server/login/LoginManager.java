package com.upl.mmorpg.game.server.login;

import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.game.server.GameStateManager;
import com.upl.mmorpg.game.server.ServerGame;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.map.GameMap;

public class LoginManager implements LoginInterface
{
	public LoginManager(ServerGame game, RPCManager rpc)
	{
		this.game = game;
		this.rpc = rpc;
	}
	
	public void setRPCManager(RPCManager rpc)
	{
		this.rpc = rpc;
	}
	
	public boolean login(String username, byte[] password)
	{
		Log.vnetln(username + " has logged in.");
		
		MMOCharacter character = game.createGoblin(12, 12, GameMap.EXAMPLE1);
		new GameStateManager(game, character, rpc);
		
		return true;
	}
	
    public boolean hello()
    {
    	return true;
    }
    
    private ServerGame game;
    private RPCManager rpc;
}
