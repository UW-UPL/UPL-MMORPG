package com.upl.mmorpg.lib.blender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.upl.mmorpg.lib.animation.AnimationManager;
import com.upl.mmorpg.lib.libfile.FileManager;

public class AnimationFormatter 
{
	public static void usage()
	{
		System.out.println("USAGE: blender-formatter <descriptor>\n");
		System.out.println("The current directory must contain the following folders:");
		System.out.println("\t\tfront, back, left, right, ");
		System.out.println("\t\tfront_right, front_left, back_right, back_left");
		System.out.println();
		System.out.println("The syntax of the descriptor file is as follows:");
		System.out.println();
		System.out.println("\t<animation_name_1>,<start_frame>,<end_frame>");
		System.out.println("\t<animation_name_2>,<start_frame>,<end_frame>");
		System.out.println("\t...");
		System.out.println("\t<animation_name_n>,<start_frame>,<end_frame>");
		System.exit(0);
	}
	
	public static String generateBlenderName(int num)
	{
		if(num < 0) return null;
		
		if(num < 10)
			return "000" + num;
		if(num < 100)
			return "00" + num;
		if(num < 1000)
			return "0" + num;
		if(num < 10000)
			return "" + num;
		return null;
	}
	
	public static void main(String args[]) throws IOException
	{
		if(args.length != 1)
			usage();
		FileManager input = new FileManager(args[0], false, true, false);
		
		ArrayList<AnimationFrame> animations = new ArrayList<AnimationFrame>();
		
		String in_line = null;
		int line_num = 0;
		while((in_line = input.readLine()) != null)
		{
			line_num++;
			in_line = in_line.trim();
			
			/* Skip empty lines */
			if(in_line.length() == 0) continue;
			/* Skip comments */
			if(in_line.startsWith("#"))
				continue;
			
			String[] parts = in_line.split(",");
			if(parts.length != 3)
			{
				System.out.println("err: syntax error on line " + line_num);
				System.out.println("Expecting: <animation_name_1>,<start_frame>,<end_frame>");
				System.out.println("Got:       " + in_line);
			}
			int start;
			int end;
			
			try
			{
				start = Integer.parseInt(parts[1]);
			} catch(Exception e)
			{
				System.out.println("err: syntax error on line " + line_num);
				System.out.println("Expecting an integer for argument 1, got: " + parts[1]);
				continue;
			}
			
			try
			{
				end = Integer.parseInt(parts[2]);
			} catch(Exception e)
			{
				System.out.println("err: syntax error on line " + line_num);
				System.out.println("Expecting an integer for argument 2, got: " + parts[2]);
				continue;
			}
			
			AnimationFrame frame = new AnimationFrame(parts[0], start, end);
			animations.add(frame);
		}
		
		/* Create all of the needed directories */
		Iterator<AnimationFrame> it = animations.iterator();
		while(it.hasNext())
		{
			AnimationFrame frame = it.next();
			FileManager.mkdirp(frame.name);
			
			for(String s : AnimationManager.directions_str)
				FileManager.mkdirp(frame.name + File.separator + s);
		}
		
		/* These should already exist */
		boolean generated = false;
		for(String s : AnimationManager.directions_str)
			generated |= FileManager.mkdirp(s);
		if(generated)
		{
			System.out.println("Directories generated. Please populate the directories");
			System.out.println("with the animation files.");
			input.close();
			System.exit(0);
		}
		
		it = animations.iterator();
		while(it.hasNext())
		{
			AnimationFrame frame = it.next();
		
			System.out.println("Generating files for animation: " + frame.name);
			for(int x = frame.start;x <= frame.end;x++)
			{
				String name = generateBlenderName(x) + ".png";
				for(String s : AnimationManager.directions_str)
				{
					System.out.print(s + File.separator + name + " --> " 
							+ frame.name + File.separator + s 
							+ File.separator + name + "  ");
					if(FileManager.cp(s + File.separator + name, 
							frame.name + File.separator + s 
							+ File.separator + name))
						System.out.println("OK");
					else System.out.println("FAIL");
				}
			}
		}
		
		input.close();
	}
	
	private static class AnimationFrame
	{
		public AnimationFrame(String name, int start, int end)
		{
			this.name = name;
			this.start = start;
			this.end = end;
		}
		
		private String name;
		private int start;
		private int end;
	}
}
