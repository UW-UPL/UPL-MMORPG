package com.upl.mmorpg.lib.liblog;

public class Log 
{
	/* Whether or not to display any output */
	public static final boolean DEBUG = true;
	/* Whether or not to display error messages */
	public static final boolean ERROR =        true && DEBUG;
	/* Whether or not to display basic output */
	public static final boolean VERBOSE =      true && DEBUG;
	/* Whether or not to display very verbose messages */
	public static final boolean VERY_VERBOSE = true && DEBUG && VERBOSE;
	
	/* Whether or not to display very verbose messages */
	public static final boolean NETWORK =      true && DEBUG && VERBOSE;
	/* Whether or not to display very verbose messages */
	public static final boolean TICKET =       true && DEBUG && VERY_VERBOSE;
	/* Whether or not to display rendering debug messages */
	public static final boolean RENDER =       false && DEBUG && VERBOSE;
	
	
	public static void v(String s)
	{
		if(VERBOSE)
			System.out.print("   VER: " + s);
	}
	
	public static void vln(String s)
	{
		if(VERBOSE)
			System.out.println("   VER: " + s);
	}
	
	public static void vok()
	{
		if(VERBOSE)
			System.out.println("[ OK ]");
	}
	
	public static void vfail()
	{
		if(VERBOSE)
			System.out.println("[FAIL]");
	}
	
	public static void vv(String s)
	{
		if(VERY_VERBOSE)
			System.out.print("   VVR: " + s);
	}
	
	public static void vvln(String s)
	{
		if(VERY_VERBOSE)
			System.out.println("   VVR: " + s);
	}
	
	public static void vvok()
	{
		if(VERY_VERBOSE)
			System.out.println("[ OK ]");
	}
	
	public static void vvfail()
	{
		if(VERY_VERBOSE)
			System.out.println("[FAIL]");
	}
	
	public static void e(String s)
	{
		if(ERROR)
			System.out.println(">> ERR: " + s);
	}
	
	public static void wtf(String s, Exception e)
	{
		if(DEBUG)
		{
			System.out.println(">> WTF: " + s);
			e.printStackTrace();
		}
	}
	
	/* Specific application logging*/
	
	/* Network layer debugging */
	public static void vnet(String s)
	{
		if(NETWORK)
			System.out.print("   NET: " + s);
	}
	public static void vnetln(String s)
	{
		if(NETWORK)
			System.out.println("   NET: " + s);
	}
	public static void vnetok()
	{
		if(NETWORK)
			System.out.println("[ OK ]");
	}
	public static void vnetfail()
	{
		if(NETWORK)
			System.out.println("[FAIL]");
	}
	
	/* Ticket manager debugging */
	public static void vtick(String s)
	{
		if(TICKET)
			System.out.print("   TIK: " + s);
	}
	public static void vtickln(String s)
	{
		if(TICKET)
			System.out.println("   TIK: " + s);
	}
	public static void vtickok()
	{
		if(TICKET)
			System.out.println("[ OK ]");
	}
	public static void vtickfail()
	{
		if(TICKET)
			System.out.println("[FAIL]");
	}
	
	/* Render debugging */
	public static void vrnd(String s)
	{
		if(RENDER)
			System.out.print("   TIK: " + s);
	}
	public static void vrndln(String s)
	{
		if(RENDER)
			System.out.println("   TIK: " + s);
	}
	public static void vrndok()
	{
		if(RENDER)
			System.out.println("[ OK ]");
	}
	public static void vrndfail()
	{
		if(RENDER)
			System.out.println("[FAIL]");
	}
}
