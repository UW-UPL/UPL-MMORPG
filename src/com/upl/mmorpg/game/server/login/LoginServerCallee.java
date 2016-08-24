package com.upl.mmorpg.game.server.login;

import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.librpc.RPCCallee;
import com.upl.mmorpg.lib.util.StackBuffer;

public class LoginServerCallee implements RPCCallee
{
    public LoginServerCallee(LoginManager client)
    {
        this.client = client;
    }

    @Override
    public void invalid_rpc(int num)
    {
        Log.e("Invalid RPC used!");
    }

    public StackBuffer __login(StackBuffer stack)
    {
        /* Pop the arguments */
        String arg0 = stack.popString();
        byte[] arg1 = stack.popByteArr();

        /* Do the function call */
        boolean result = client.login(arg0, arg1);
        /* Make a result stack */
        StackBuffer ret_stack = new StackBuffer();
        ret_stack.pushBoolean(result);
        return ret_stack;
    }

    public StackBuffer __hello(StackBuffer stack)
    {
        /* Pop the arguments */

        /* Do the function call */
        boolean result = client.hello();
        /* Make a result stack */
        StackBuffer ret_stack = new StackBuffer();
        ret_stack.pushBoolean(result);
        return ret_stack;
    }
    
    @Override
    public StackBuffer handle_call(StackBuffer stack)
    {
        /* Get the function number */
        int func_num = stack.popInt();
        /* We are expecting a result stack buffer */
        StackBuffer result = null;
        switch(func_num)
        {
            case 1: /** login */
                result = __login(stack);
                break;
            case 2: /** hello */
                result = __hello(stack);
                break;
            default:
                invalid_rpc(func_num);
                break;
        };

        return result;
    }

    private LoginManager client;
}
