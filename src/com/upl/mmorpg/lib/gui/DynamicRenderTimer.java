package com.upl.mmorpg.lib.gui;

public class DynamicRenderTimer implements Runnable
{
	public DynamicRenderTimer(int fps, Runnable run)
	{
		this.listener = run;
		wait = RenderMath.calculateVSYNC(fps);
	}
	
	public void startTimer()
	{
		running = true;
		runningThread = new Thread(this);
		runningThread.start();
		
		lastUpdate = System.nanoTime();
	}
	
	public void stopTimer()
	{
		running = false;
		try {runningThread.interrupt();} catch(Exception e) {}
		try {runningThread.join(1000);} catch(Exception e) {}		
		runningThread = null;
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			long nanosPassed = System.nanoTime() - lastUpdate;
			lastUpdate = System.nanoTime();
			/* Do the rendering */
			listener.run();
			
			/* Calculate the time the java scheduler wasted */
			int millisPassed = (int)(nanosPassed / 1000000);
			int javaWastedTime = millisPassed - wait;
			if(javaWastedTime < 0)
				javaWastedTime = 0;
			
			try {Thread.sleep(wait - javaWastedTime);} catch(Exception e){}
		}
	}
	
	private Runnable listener;
	private boolean running;
	private Thread runningThread;
	private int wait;
	private long lastUpdate;
}
