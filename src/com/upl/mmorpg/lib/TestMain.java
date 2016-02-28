package com.upl.mmorpg.lib;

import java.util.Arrays;

import com.upl.mmorpg.lib.liblog.Log;

public class TestMain 
{
	public static void main(String args[])
	{
		StackBuffer stack = new StackBuffer();
		
		stack.pushIntArr(new int[]{0, 1000, 1000000, 12345678});
		stack.pushString("Is");
		stack.pushString("A");
		stack.pushString("StackBuffer");
		
		byte arr[] = stack.toArray();
		stack = new StackBuffer(arr);
		Log.vln("0: " + Arrays.toString(stack.popIntArr()));
		Log.vln("1: " + stack.popString());
		Log.vln("2: " + stack.popString());
		Log.vln("3: " + stack.popString());
		Log.vln("arr sz: " + arr.length);
	}
}
