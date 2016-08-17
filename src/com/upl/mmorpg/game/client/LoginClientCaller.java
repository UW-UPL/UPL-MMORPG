package com.upl.mmorpg.game.client;

import com.upl.mmorpg.game.server.login.LoginInterface;
import com.upl.mmorpg.lib.librpc.RPCManager;
import com.upl.mmorpg.lib.util.StackBuffer;

public class LoginClientCaller implements LoginInterface
{
    public LoginClientCaller(RPCManager rpc)
    {
        this.rpc = rpc;
    }

    public boolean login(String arg0, byte[] arg1)
    {
        StackBuffer stack = new StackBuffer();

        /* Push the function number */
        stack.pushInt(1);
        /* Push the arguments */
        stack.pushString(arg0);
        stack.pushByteArr(arg1);
        /* Do the network call */
        StackBuffer res = rpc.do_call(stack, true);
        return res.popBoolean();
    }

    public boolean hello()
    {
        StackBuffer stack = new StackBuffer();

        /* Push the function number */
        stack.pushInt(2);
        /* Push the arguments */
        /* Do the network call */
        StackBuffer res = rpc.do_call(stack, true);
        return res.popBoolean();
    }

    private RPCManager rpc;
}
