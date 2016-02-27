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
		String callee_receiver_object = "game";
		String callee_prefix_args = "player_info";
		
		if(args.length != 1)
		{
			usage();
			return;
		}
		
		FileManager file = new FileManager(args[0], true, false);
		FileManager interface_file = new FileManager(args[0] + ".int", 
				true, false, true);
		FileManager caller_stubs = new FileManager(args[0] + ".caller", 
				true, false, true);
		FileManager callee_stubs = new FileManager(args[0] + ".callee", 
				true, false, true);
		FileManager handler = new FileManager(args[0] + ".handler", 
				true, false, true);
		
		/* Modify the inputs for ease */
		callee_receiver_object = callee_receiver_object + ".";
		
		/* Setup the basic stuff for the handler */
		handler.println("\t\tStackBuffer result = null;");
		handler.println("\t\tswitch(func_num)");
		handler.println("\t\t{");
		
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
			String func_name = parts[1].trim();
			if(ret_type == null)
			{
				Log.e("Syntax error, line " + line);
				Log.e("\tNot a valid ret type: " + parts[0]);
				continue;
			}
			
			ret_type = ret_type.trim();
			
			String header = new String("\tpublic " 
					+ ret_type + " "
					+ func_name);
			String callee_header = new String("\tpublic " 
					+ ret_type + " "
					+ "__" + func_name);
	
			StringBuilder prototype = new StringBuilder("(");
			StringBuilder callee_args = new StringBuilder("("
					+ callee_prefix_args);
			
			/* Parse function arguments -- do proto and callee_args */
			String arg_types[] = new String[parts.length - 2];
			int arg_num = 0;
			for(arg_num = 0;arg_num < parts.length - 2;arg_num++)
			{
				callee_args.append(", ");
				if(arg_num != 0)
					prototype.append(", ");
				
				int pos = arg_num + 2;
				String arg_type = StackBuffer.typeSupported(parts[pos].trim());
				if(arg_type == null)
				{
					Log.e("Syntax error, line " + line);
					Log.e("\tNot a valid type: " + parts[pos]);
					continue;
				}
				
				prototype.append(arg_type + " arg" + arg_num);
				arg_types[arg_num] = arg_type;
				callee_args.append("arg" + arg_num);
			}
			prototype.append(")");
			callee_args.append(")");
			
			/* Write the header to all 3 files */
			interface_file.println(header.toString() 
					+ prototype.toString() + ";");
			caller_stubs.println(header.toString() + prototype.toString());
			callee_stubs.println(callee_header.toString() 
					+ "(StackBuffer stack)");
			
			/* Generate the callee function entry */
			caller_stubs.println("\t{");
			caller_stubs.println("\t\tStackBuffer stack = new StackBuffer();");
			caller_stubs.println("");
			caller_stubs.println("\t\t/* Push the function number */");
			caller_stubs.println("\t\tstack.pushInt(" + func_num + ");");
			caller_stubs.println("\t\t/* Push the arguments */");
			
			/* Generate the callee function entry */
			callee_stubs.println("\t{");
			callee_stubs.println("\t\t/* Pop the arguments */");
			
			/* Generate the start of a handler case */
			handler.println("\t\t\tcase " + func_num 
					+ ": /** " + func_name + " */");
			handler.println("\t\t\t\tresult = __" + func_name + "(stack);");
			handler.println("\t\t\t\tbreak;");
			
			/* Do any needed pushing or popping off of the stack */
			for(int x = 0;x < arg_types.length;x++)
			{
				String arg_type = arg_types[x];
				/* Caller arguments */
				caller_stubs.println("\t\t" 
						+ StackBuffer.getPushMethod(arg_type) 
						+ "(arg" + x + ");");
				/* Callee arguments */
				callee_stubs.println("\t\t" + arg_type
						+ " arg" + x + " = stack."
						+ StackBuffer.getPopMethod(arg_type) + "();");
			}
			
			caller_stubs.println("\t\t/* Do the network call */");
			caller_stubs.println("\t\tStackBuffer res = rpc.do_call(stack);");
			caller_stubs.println("\t\treturn res." 
					+ StackBuffer.getPopMethod(ret_type) + "();");
			caller_stubs.println("\t}");
			caller_stubs.println("");
			
			callee_stubs.println("");
			callee_stubs.println("\t\t/* Do the function call */");
			callee_stubs.println("\t\treturn " + callee_receiver_object 
					+ func_name  + callee_args.toString() + ";");
			callee_stubs.println("\t}");
			
			line++;
			func_num++;
		}
		
		handler.println("\t\t\tdefault:");
		handler.println("\t\t\t\tinvalid_rpc(func_num);");
		handler.println("\t\t\t\tbreak;");
		handler.println("\t\t};");
		
		file.close();
		interface_file.close();
		caller_stubs.close();
		callee_stubs.close();
	}
}
