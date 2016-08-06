package com.upl.mmorpg.lib.librpc;

import java.io.IOException;

import com.upl.mmorpg.lib.libfile.FileManager;
import com.upl.mmorpg.lib.liblog.Log;
import com.upl.mmorpg.lib.util.StackBuffer;

public class RPCStubGenerator 
{
	public static void usage()
	{
		Log.e("USAGE: RPCStubGenerator <desc-file1> ...");
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
		String callee_receiver_object = "client";
		String callee_prefix_args = "player_info";
		boolean callee_prefix = false;

		if(args.length < 1)
		{
			usage();
			return;
		}
		
		/* Modify the inputs for ease */
		callee_receiver_object = callee_receiver_object + ".";

		String file_name;

		for(int file_idx = 0;file_idx < args.length;file_idx++)
		{
			file_name = args[file_idx];
			
			FileManager file = new FileManager(file_name, true, false);
			FileManager interface_file = new FileManager(file_name + ".int", 
					true, false, true);
			FileManager caller_stubs = new FileManager(file_name + ".caller", 
					true, false, true);
			FileManager callee_stubs = new FileManager(file_name + ".callee", 
					true, false, true);
			FileManager handler = new FileManager(file_name + ".handler", 
					true, false, true);

			

			/* Setup the basic stuff for the handler */
			handler.println("\t@Override");
			handler.println("\tpublic StackBuffer handle_call(StackBuffer stack)");
			handler.println("\t{");
			handler.println("\t\t/* Get the function number */");
			handler.println("\t\tint func_num = stack.popInt();");
			handler.println("\t\t/* We are expecting a result stack buffer */");
			handler.println("\t\tStackBuffer result = null;");
			handler.println("\t\tswitch(func_num)");
			handler.println("\t\t{");
			
			caller_stubs.println("\tpublic ClassName(RPCManager rpc)");
			caller_stubs.println("\t{");
			caller_stubs.println("\t\tthis.rpc = rpc;");
			caller_stubs.println("\t}");
			caller_stubs.println("");
			
			callee_stubs.println("\tpublic ClassName(ClientHandler client)");
			callee_stubs.println("\t{");
			callee_stubs.println("\t\tthis.client = client;");
			callee_stubs.println("\t}");
			callee_stubs.println("");
			callee_stubs.println("\t@Override");
			callee_stubs.println("\tpublic void invalid_rpc(int num) ");
			callee_stubs.println("\t{");
			callee_stubs.println("\t\tLog.e(\"Invalid RPC used!\");");
			callee_stubs.println("\t}");
			callee_stubs.println("");
			
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

				String block = "true";
				String ret_type = null;
				if(parts[0].trim().equalsIgnoreCase("void"))
				{
					block = "false";
					ret_type = "void";
				}else StackBuffer.typeSupported(parts[0]);
				
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
						+ (block.equalsIgnoreCase("true") ? "StackBuffer" : "void") 
						+ " __" + func_name);

				StringBuilder prototype = new StringBuilder("(");
				StringBuilder callee_args = new StringBuilder("("
						+ (callee_prefix ? callee_prefix_args : ""));

				/* Parse function arguments -- do proto and callee_args */
				String arg_types[] = new String[parts.length - 2];
				int arg_num = 0;
				for(arg_num = 0;arg_num < parts.length - 2;arg_num++)
				{
					if(callee_prefix || arg_num > 0)callee_args.append(", ");
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
				if(block.equalsIgnoreCase("true"))
				{
					handler.println("\t\t\t\tresult = __" + func_name + "(stack);");
				} else {
					handler.println("\t\t\t\t__" + func_name + "(stack);");
				}
				handler.println("\t\t\t\tbreak;");

				/* Do any needed pushing or popping off of the stack */
				for(int x = 0;x < arg_types.length;x++)
				{
					String arg_type = arg_types[x];
					/* Caller arguments */
					caller_stubs.println("\t\tstack." 
							+ StackBuffer.getPushMethod(arg_type) 
							+ "(arg" + x + ");");
					/* Callee arguments */
					callee_stubs.println("\t\t" + arg_type
							+ " arg" + x + " = stack."
							+ StackBuffer.getPopMethod(arg_type) + "();");
				}

				caller_stubs.println("\t\t/* Do the network call */");
				if(block.equalsIgnoreCase("true"))
				{
					caller_stubs.println("\t\tStackBuffer res = rpc.do_call(stack, " 
							+ block + ");");
					caller_stubs.println("\t\treturn res." 
							+ StackBuffer.getPopMethod(ret_type) + "();");
				} else {
					caller_stubs.println("\t\trpc.do_call(stack, false);");
				}
				caller_stubs.println("\t}");
				caller_stubs.println("");

				callee_stubs.println("");
				callee_stubs.println("\t\t/* Do the function call */");
				if(block.equalsIgnoreCase("true"))
				{
					callee_stubs.println("\t\t" + ret_type + " result = " 
							+ callee_receiver_object 
							+ func_name  + callee_args.toString() + ";");
				} else {
					callee_stubs.println("\t\t" + callee_receiver_object 
							+ func_name  + callee_args.toString() + ";");
				}
				
				/* Only send a response if we care about it */
				if(block.equalsIgnoreCase("true"))
				{
					callee_stubs.println("\t\t/* Make a result stack */");
					callee_stubs.println("\t\tStackBuffer ret_stack = " 
							+ "new StackBuffer();");
					callee_stubs.println("\t\tret_stack." 
							+ StackBuffer.getPushMethod(ret_type) + "(result);");
					callee_stubs.println("\t\treturn ret_stack;");
				}
				callee_stubs.println("\t}");
				callee_stubs.println("");

				line++;
				func_num++;
			}

			handler.println("\t\t\tdefault:");
			handler.println("\t\t\t\tinvalid_rpc(func_num);");
			handler.println("\t\t\t\tbreak;");
			handler.println("\t\t};");
			handler.println("");
			handler.println("\t\treturn result;");
			handler.println("\t}");
			
			caller_stubs.println("\tprivate RPCManager rpc;");
			
			callee_stubs.println("\tprivate ClientHandler client;");

			file.close();
			interface_file.close();
			caller_stubs.close();
			callee_stubs.close();
		}
	}
}
