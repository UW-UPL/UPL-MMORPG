package com.upl.mmorpg.lib.libfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;

public class FileManager 
{
	private void open(String path, boolean create, 
			boolean read_perm, boolean write_perm) throws IOException
	{
		this.path = path;
		this.read_perm = read_perm;
		this.write_perm = write_perm;
		this.references = 1;
		Log.vvln("Opening file: " + path);
		opened = false;
		file = new File(path);

		if(create)
		{
			if(!file.exists())
			{
				if(write_perm)
				{
					Log.vvln("File doesn't exist: " + path);
					file.createNewFile();
					Log.vvln("File create success.");
				} else {
					Log.vvln("File doesn't exist and no wr perm: " + path);
					return;
				}
			} else {
				Log.vvln("File exists.");
			}
		}

		if(file.exists())
		{
			if(read_perm)
				fis = new FileInputStream(file);
			else fis = null;
			if(write_perm)
				fos = new FileOutputStream(file);
			else fos = null;

			opened = true;
		} else {
			Log.e("Warning: File not opened: " + path);
		}

		Log.vvln("Opening file " + path + " success.");
	}

	/**
	 * Open an existing file.
	 * @param path The path of the file.
	 * @param read Whether read permission is needed.
	 * @param write Whether write permission is needed.
	 */
	public FileManager(String path, boolean read, boolean write)
	{
		try
		{
			open(path, false, read, write);
		} catch(IOException e){}
	}

	/**
	 * Open a new or existing file.
	 * @param path The path of the file.
	 * @param create Whether or not to create the file.
	 * @param read Whether read permission is needed.
	 * @param write Whether write permission is needed.
	 * @throws IOException The IOException that has occurred.
	 */
	public FileManager(String path, boolean create,
			boolean read, boolean write) throws IOException
	{
		open(path, create, read, write);
	}

	/**
	 * Make a directory and all of it's parent's.
	 * @param dir The directory to create.
	 * @return Whether or not the parents and the directory could be created.
	 */
	public static boolean mkdirp(String dir)
	{
		boolean result = false;
		File f = null;

		try
		{
			f = new File(dir);
			result = f.mkdirs();
		} catch(Exception e)
		{
			result = false;
		}

		f = null;
		return result;
	}

	/**
	 * Make a directory.
	 * @param dir The directory to create.
	 * @return Whether or not the directory could be created.
	 */
	public static boolean mkdir(String dir)
	{
		boolean result = false;
		File f = null;

		try
		{
			f = new File(dir);
			result = f.mkdir();
		} catch(Exception e)
		{
			result = false;
		}

		f = null;
		return result;
	}

	/**
	 * Copy a file from one place to another.
	 * @param path The original file path.
	 * @param copy The place to copy to.
	 * @return Whether or not the file could be copied.
	 * @throws IOException If an IOException occurs.
	 */
	public static boolean cp(String path, String copy) throws IOException
	{
		FileManager input = new FileManager(path, false, true, false);
		FileManager output = new FileManager(copy, true, true, true);

		if(!input.opened() || !output.opened())
		{
			input.close();
			output.close();
			return false;
		}

		while(true)
		{
			byte[] buffer = input.readBytes(8192);
			output.writeBytes(buffer);
			if(buffer.length < 8192)
				break;
		}

		input.close();
		output.close();

		return true;
	}

	/**
	 * Get a list of files in a directory.
	 * @param dir The directory to search in.
	 * @return The names of the files in the directory.
	 */
	public static String[] getFiles(String dir)
	{
 		String[] result = null;

		File f = null;

		try
		{
			f = new File(dir);
			File[] files = f.listFiles();
			result = new String[files.length];
			for(int x = 0;x < files.length;x++)
			{
				result[x] = files[x].getName();
				files[x] = null;
			}
			files = null;
		}catch(Exception e)
		{
			result = null;
		}

		f = null;

		return result;
	}

	/**
	 * Get an array of all of the directory names in the directory.
	 * @param dir The directory.
	 * @return The names of the directory in the directory.
	 */
	public static String[] getDirectories(String dir)
	{
		String[] result = null;
		LinkedList<String> list = new LinkedList<String>();

		File f = null;

		try
		{
			f = new File(dir);
			File[] files = f.listFiles();
			for(int x = 0;x < files.length;x++)
			{
				if(files[x].isDirectory())
					list.add(files[x].getName());
				files[x] = null;
			}
			files = null;
		}catch(Exception e)
		{
			result = null;
		}

		f = null;

		result = new String[list.size()];
		int x = 0;
		Iterator<String> it = list.iterator();
		while(it.hasNext())
		{
			result[x] = it.next();
			x++;
		}

		return result;
	}

	/**
	 * Write bytes to a file.
	 * @param bytes The bytes to write.
	 * @return Whether or not the bytes were written.
	 */
	public boolean writeBytes(byte bytes[])
	{
		if(!write_perm) return false;
		
		Log.vvln("Writing " + bytes.length + " to file: " + path);
		try 
		{
			fos.write(bytes, 0, bytes.length);
			Log.vvln("Write success.");
		} catch (IOException e) {
			Log.wtf("Failed to write to file: " + path, e);
			return false;
		}

		return true;
	}

	/**
	 * Write a String to a file.
	 * @param s The string to write.
	 * @return Whether or not the string was written.
	 */
	public boolean print(String s)
	{
		if(!write_perm) return false;
		
		return writeBytes(s.getBytes());
	}

	/**
	 * Write a String to a file.
	 * @param s The string to write.
	 * @return Whether or not the string was written.
	 */
	public boolean println(String s)
	{
		if(!write_perm) return false;
		
		StringBuilder build = new StringBuilder();
		build.append(s);
		build.append("\n");

		return writeBytes(build.toString().getBytes());
	}

	/**
	 * Read bytes from a file.
	 * @param len The length to read.
	 * @return The bytes read from the file. Null on failure.
	 */
	public byte[] readBytes(int len)
	{
		if(!read_perm) return null;
		
		byte arr[] = new byte[len];

		try 
		{
			int index = 0;
			while(index != len)
			{
				int read_len = fis.read(arr, index, len - index);

				/* Is the read complete? */
				if(read_len < 0)
				{
					byte newarr[] = new byte[index];
					for(int x = 0;x < index;x++)
						newarr[x] = arr[x];
					arr = null;
					arr = newarr;
					len = index;
					break;
				}

				index += read_len;
			}
		} catch (IOException e) {
			Log.wtf("Failed to read bytes from file: " + path, e);
			arr = null;
			return null;
		}

		return arr;
	}

	/**
	 * Read a line from the file
	 * @return The string read from the file
	 */
	public String readLine()
	{
		if(!read_perm) return null;
		
		StringBuilder build = new StringBuilder();

		byte buffer[] = new byte[1];

		boolean successful_read = false;
		try 
		{
			while(fis.read(buffer, 0, 1) > 0)
			{
				char c = (char)buffer[0];

				build.append(c);

				if(c == '\n') 
				{
					successful_read = true;
					break;
				}
			}
		} catch (IOException e) 
		{
			Log.wtf("Failed to read line from file: " + path, e);
			return null;
		}

		/* Did we get anything? */
		if(build.length() == 0)
			return null;

		if(!successful_read)
		{
			Log.e("didn't complete full read on file: " + path);
			return null;
		}

		return build.toString();
	}

	/**
	 * Returns whether the file was able to be opened.
	 * @return The state of the file 
	 */
	public boolean opened()
	{
		return opened;
	}
	
	/**
	 * Add a reference to the file. This is called when this file is opened
	 * multiple times.
	 */
	public void addReference()
	{
		references++;
	}

	/**
	 * Close the file. If this isn't the last reference to the file,
	 * the file will remain open.
	 */
	public void close()
	{
		if(references < 1)
			return;
		else if(references == 1)
		{
			Log.vvln("Closed file: " + path);
	
			if(write_perm)
			{
				/* Flush anything in the output pipe */
				try { fos.flush(); } catch(Exception e){}
				try { fos.close(); } catch(Exception e){}
			}
	
			if(read_perm)
			{
				try { fis.close(); } catch(Exception e){}
			}
	
			fos = null;
			fis = null;
			file = null;
			opened = false;
			path = null;
		} else references--;
	}

	/**
	 * Delete and close the file.
	 */
	public void deleteAndClose()
	{
		file.delete();
		close();
	}

	/**
	 * Returns the length of the file.
	 * @return The length of the file.
	 */
	public long length()
	{
		return file.length();
	}
	
	/**
	 * Returns the path that was used to open the file.
	 * @return The path that was used to open the file.
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * Returns the absolute path of the file.
	 * @return The absolute path of the file.
	 */
	public String getAbsolutePath()
	{
		if(!opened)
			return null;
		else return file.getAbsolutePath();
	}

	private File file;
	private FileInputStream fis;
	private FileOutputStream fos;
	private boolean opened;
	private boolean read_perm;
	private boolean write_perm;
	private String path;
	private int references;
}
