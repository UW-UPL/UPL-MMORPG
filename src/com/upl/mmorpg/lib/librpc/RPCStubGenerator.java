package com.upl.mmorpg.lib.librpc;

import java.io.IOException;

import com.upl.mmorpg.lib.StackBuffer;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.libfile.FileManager;

public class RPCStubGenerator 
{
	public static void usage()
	{
		Log.e("USAGE: RPCStubGenerator <desc-file>");
		Log.e("");
		Log.e("Format of the file is as follows:");
		Log.e("\t<func-return-val>,<func-name>,<func-arg1>,<func-arg2>,...");
		Log.e("");
		Log.e("Example:");
		Log.e("\tint,get_health,String");
		Log.e("\tString,get_playername");
	}
	
	public static void main(String[] args) throws IOException 
	{
		if(args.length != 1)
		{
			usage();
			return;
		}
		
		FileManager file = new FileManager(args[0]);
		FileManager interface_file = new FileManager(args[0] + ".int", true);
		FileManager caller_stubs = new FileManager(args[0] + ".caller", true);
		FileManager callee_stubs = new FileManager(args[0] + ".callee", true);
		
		String s;
		int line = 0;
		int func_num = 1;
		while((s = file.readLine()) != null)
		{
			if(s.trim().equals("") || s.trim().startsWith("#"))
			{
				line++;
				continue;
			}
			
			String parts[] = s.split(",");
			
			if(parts.length < 2)
			{
				Log.e("Syntax error, line " + line);
				Log.e("\tNot enough arguments!");
				continue;
			}
			
			String ret_type = StackBuffer.typeSupported(parts[0]);
			if(ret_type == null)
			{
				Log.e("Syntax error, line " + line);
				Log.e("\tNot a valid type: " + parts[0]);
				continue;
			}
			
			String header = new String("public " 
					+ ret_type + " "
					+ parts[1].trim());
			String callee_header = new String("public " 
					+ ret_type + " "
					+ "__" + parts[1].trim());
	
			StringBuilder arg_list = new StringBuilder("(");
			/* Parse arguments */
			String arg_types[] = new String[parts.length - 2];
			int arg_num = 0;
			for(arg_num = 0;arg_num < parts.length - 2;arg_num++)
			{
				if(arg_num != 0)
					arg_list.append(",");
				
				int pos = arg_num + 2;
				String arg_type = StackBuffer.typeSupported(parts[pos]);
				if(arg_type == null)
				{
					Log.e("Syntax error, line " + line);
					Log.e("\tNot a valid type: " + parts[0]);
					continue;
				}
				
				arg_list.append(arg_type + " arg" + arg_num);
				arg_types[arg_num] = arg_type;
			}
			
			arg_list.append(")");
			
			/* Write the header to all 3 files */
			interface_file.println(header.toString() + ";");
			caller_stubs.println(header.toString());
			callee_stubs.println(callee_header.toString());
			
			/* Generate the caller stubs */
			caller_stubs.println("{");
			caller_stubs.println("\tStackBuffer stack = new StackBuffer();");
			caller_stubs.println("");
			caller_stubs.println("\t/* Push the function number */");
			caller_stubs.println("\tstack.pushInt(" + func_num + ")");
			caller_stubs.println("\t/* Push the arguments */");
			for(int x = 0;x < arg_types.length;x++)
			{
				String arg_type = arg_types[x];
				caller_stubs.println("\t" 
						+ StackBuffer.getPushMethod(arg_type) 
						+ "(arg" + x + ");");
			}
			caller_stubs.println("\t/* Do the network call */");
			caller_stubs.println("\trpc.do_call(stack);");
			caller_stubs.println("\tStackBuffer res = rpc.do_call(stack);");
			caller_stubs.println("\treturn res." 
					+ StackBuffer.getPopMethod(ret_type) + "();");
			caller_stubs.println("}");
			
			line++;
			func_num++;
		}
		
		file.close();
		interface_file.close();
		caller_stubs.close();
		callee_stubs.close();
	}
}
