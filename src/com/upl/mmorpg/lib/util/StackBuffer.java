package com.upl.mmorpg.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.LinkedList;

public class StackBuffer 
{
	public StackBuffer()
	{
		/* Using the stack buffer in read mode */
		rbuff = null;
		rbuff_pos = -1;
		chunks = new LinkedList<byte[]>();
	}
	
	public StackBuffer(byte arr[])
	{
		/* Using the stack buffer in write mode */
		rbuff = arr;
		rbuff_pos =  0;
		chunks = null;
	}
	
	/** Methods for pushing stuff into the buffer */
	
	public void pushByte(byte b)
	{
		byte buffer[] = new byte[1];
		buffer[0] = b;
		chunks.add(buffer);
	}
	
	public void pushChar(byte c)
	{
		byte buffer[] = new byte[1];
		buffer[0] = (byte)c;
		chunks.add(buffer);
	}
	
	public void pushBoolean(boolean b)
	{
		pushByte((byte)(b ? 1 : 0));
	}
	
	public void pushInt(int i)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(i);
		chunks.add(buffer.array());
	}
	
	public void pushLong(long l)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putLong(l);
		chunks.add(buffer.array());
	}
	
	public void pushByteArr(byte[] arr)
	{
		/* Create a copy */
		byte copy[] = new byte[arr.length];
		for(int x = 0;x < arr.length;x++)
			copy[x] = arr[x];
		
		/* Add the copy */
		chunks.add(copy);
	}
	
	public void pushByteArrDirect(byte[] arr)
	{
		chunks.add(arr);
	}
	
	public void pushIntArr(int[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			pushInt(arr[x]);
	}
	
	public void pushLongArr(long[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			pushLong(arr[x]);
	}
	
	public void pushString(String str)
	{
		pushInt(str.length());
		pushByteArrDirect(str.getBytes());
	}
	
	public void pushStringArr(String[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			pushString(arr[x]);
	}
	
	public void pushFloat(float f)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putFloat(f);
		chunks.add(buffer.array()); 
	}
	
	public void pushFloatArr(float[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			pushFloat(arr[x]);
	}
	
	public void pushDouble(double d)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putDouble(d);
		chunks.add(buffer.array()); 
	}
	
	public void pushDoubleArr(double[] arr)
	{
		pushInt(arr.length);
		for(int x = 0;x < arr.length;x++)
			pushDouble(arr[x]);
	}
	
	public void pushObject(Serializable obj)
	{
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		byte[] arr = null;
		
		try
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			arr = bos.toByteArray();
		} catch(Exception e){}
		
		try {bos.close();} catch(Exception e){}
		try {oos.close();} catch(Exception e){}
		bos = null;
		oos = null;
		
		if(arr != null)
			chunks.add(arr);
		else throw new RuntimeException("Couldn't serialize object: " + obj);
	}
	
	/** Methods for popping stuff out of the buffer */
	
	public byte popByte()
	{
		rbuff_pos++;
		return rbuff[rbuff_pos - 1];
	}
	
	public char popChar()
	{
		rbuff_pos++;
		return (char)rbuff[rbuff_pos - 1];
	}
	
	public boolean popBoolean()
	{
		return popByte() == 0 ? false : true;
	}
	
	public int popInt()
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		for(int x = 0;x < 4;x++)
			buffer.put(rbuff[x + rbuff_pos]);
		rbuff_pos += 4;
		buffer.position(0);
		return buffer.getInt();
	}
	
	public long popLong()
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		for(int x = 0;x < 8;x++)
			buffer.put(rbuff[x + rbuff_pos]);
		rbuff_pos += 8;
		buffer.position(0);
		return buffer.getLong();
	}
	
	public byte[] popByteArr()
	{
		int len = popInt();
		byte arr[] = new byte[len];
		for(int x = 0;x < len;x++)
			arr[x] = popByte();
		return arr;
	}
	
	public int[] popIntArr()
	{
		int len = popInt();
		int arr[] = new int[len];
		for(int x = 0;x < len;x++)
			arr[x] = popInt();
		return arr;
	}
	
	public long[] popLongArr()
	{
		int len = popInt();
		long arr[] = new long[len];
		for(int x = 0;x < len;x++)
			arr[x] = popLong();
		return arr;
	}
	
	public String popString()
	{
		byte arr[] = popByteArr();
		return new String(arr);
	}
	
	public String[] popStringArr()
	{
		int len = popInt();
		String arr[] = new String[len];
		for(int x = 0;x < len;x++)
			arr[x] = popString();
		return arr;
	}
	
	public float popFloat()
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		for(int x = 0;x < 4;x++)
			buffer.put(rbuff[x + rbuff_pos]);
		rbuff_pos += 4;
		buffer.position(0);
		return buffer.getFloat();
	}
	
	public float[] popFloatArr()
	{
		int len = popInt();
		float arr[] = new float[len];
		for(int x = 0;x < len;x++)
			arr[x] = popFloat();
		return arr;
	}
	
	public double popDouble()
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		for(int x = 0;x < 8;x++)
			buffer.put(rbuff[x + rbuff_pos]);
		rbuff_pos += 8;
		buffer.position(0);
		return buffer.getDouble(); 
	}
	
	public double[] popDoubleArr()
	{
		int len = popInt();
		double arr[] = new double[len];
		for(int x = 0;x < len;x++)
			arr[x] = popDouble();
		return arr;
	}
	
	public Object popObject()
	{
		ByteArrayInputStream bin = null;
		ObjectInputStream ois = null;
		Object result = null;
		
		try
		{
			bin = new ByteArrayInputStream(rbuff);
			bin.skip(rbuff_pos);
			ois = new ObjectInputStream(bin);
			result = ois.readObject();
		} catch(Exception e){ result = null; }
		
		try {bin.close();} catch(Exception e){}
		try {ois.close();} catch(Exception e){}
		bin = null;
		ois = null;
		
		if(result == null)
			throw new RuntimeException("Failed to read object from stream.");
		
		return result;
	}
	
	/**
	 * Turn the stack into a single array.
	 * @return The array representation of the stack.
	 */
	public byte[] toArray()
	{
		int len = 0;
		
		/* Calculate the total length */
		Iterator<byte[]> it = chunks.iterator();
		while(it.hasNext())
			len += it.next().length;
		
		/* Create the final buffer */
		int pos = 0;
		byte arr[] = new byte[len];
		it = chunks.iterator();
		while(it.hasNext())
		{
			byte tocopy[] = it.next();
			for(int x = 0;x < tocopy.length;x++)
				arr[x + pos] = tocopy[x];
			
			pos += tocopy.length;
		}
		
		return arr;
	}
	
	public void appendFlag(int flag)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(flag);
		chunks.addFirst(buffer.array());
	}
	
	public static String typeSupported(String type)
	{
		if(type.compareToIgnoreCase("char") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("char[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("byte") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("byte[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("boolean") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("int") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("int[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("long") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("long[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("float") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("float[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("double") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("double[]") == 0)
			return type.toLowerCase();
		if(type.compareToIgnoreCase("string") == 0)
			return "String";
		if(type.compareToIgnoreCase("string[]") == 0)
			return "String[]";
		if(type.compareToIgnoreCase("object") == 0)
			return "Object";
		
		return null;
	}
	
	public static String getPopMethod(String type)
	{
		if(type.compareToIgnoreCase("char") == 0)
			return "popChar";
		if(type.compareToIgnoreCase("char[]") == 0)
			return "popCharArr";
		if(type.compareToIgnoreCase("byte") == 0)
			return "popByte";
		if(type.compareToIgnoreCase("byte[]") == 0)
			return "popByteArr";
		if(type.compareToIgnoreCase("boolean") == 0)
			return "popBoolean";
		if(type.compareToIgnoreCase("int") == 0)
			return "popInt";
		if(type.compareToIgnoreCase("int[]") == 0)
			return "popIntArr";
		if(type.compareToIgnoreCase("long") == 0)
			return "popLong";
		if(type.compareToIgnoreCase("long[]") == 0)
			return "popLongArr";
		if(type.compareToIgnoreCase("float") == 0)
			return "popFloat";
		if(type.compareToIgnoreCase("float[]") == 0)
			return "popFloatArr";
		if(type.compareToIgnoreCase("double") == 0)
			return "popDouble";
		if(type.compareToIgnoreCase("double[]") == 0)
			return "popDoubleArr";
		if(type.compareToIgnoreCase("string") == 0)
			return "popString";
		if(type.compareToIgnoreCase("string[]") == 0)
			return "popStringArr";
		if(type.compareToIgnoreCase("object") == 0)
			return "popObject";
		
		return null;
	}
	
	public static String getPushMethod(String type)
	{
		if(type.compareToIgnoreCase("char") == 0)
			return "pushChar";
		if(type.compareToIgnoreCase("char[]") == 0)
			return "pushCharArr";
		if(type.compareToIgnoreCase("byte") == 0)
			return "pushByte";
		if(type.compareToIgnoreCase("byte[]") == 0)
			return "pushByteArr";
		if(type.compareToIgnoreCase("boolean") == 0)
			return "pushBoolean";
		if(type.compareToIgnoreCase("int") == 0)
			return "pushInt";
		if(type.compareToIgnoreCase("int[]") == 0)
			return "pushIntArr";
		if(type.compareToIgnoreCase("long") == 0)
			return "pushLong";
		if(type.compareToIgnoreCase("long[]") == 0)
			return "pushLongArr";
		if(type.compareToIgnoreCase("float") == 0)
			return "pushFloat";
		if(type.compareToIgnoreCase("float[]") == 0)
			return "pushFloatArr";
		if(type.compareToIgnoreCase("double") == 0)
			return "pushDouble";
		if(type.compareToIgnoreCase("double[]") == 0)
			return "pushDoubleArr";
		if(type.compareToIgnoreCase("string") == 0)
			return "pushString";
		if(type.compareToIgnoreCase("string[]") == 0)
			return "pushStringArr";
		if(type.compareToIgnoreCase("object") == 0)
			return "pushObject";
		
		return null;
	}
	
	private LinkedList<byte[]> chunks;
	private int rbuff_pos;
	private byte[] rbuff;
}
