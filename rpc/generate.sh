#!/bin/bash

cd login
java -cp ../../bin com.upl.mmorpg.lib.librpc.RPCStubGenerator ./client-server.rpc
java -cp ../../bin com.upl.mmorpg.lib.librpc.RPCStubGenerator ./server-client.rpc
