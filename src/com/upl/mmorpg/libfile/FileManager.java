package com.upl.mmorpg.libfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.upl.mmorpg.lib.liblog.Log;

public class FileManager 
{
	private void open(String path, boolean create) throws IOException
	{
		Log.vvln("Opening file: " + path);
		opened = false;
		file = new File(path);

		if(create)
		{
			if(!file.exists())
			{
				Log.vvln("File doesn't exist: " + path);
				file.createNewFile();
				Log.vvln("File create success.");
			} else {
				Log.vvln("File exists.");
			}
		}

		if(file.exists())
		{
			fis = new FileInputStream(file);
			fos = new FileOutputStream(file);
			opened = true;
		} else {
			Log.e("Warning: File not opened: " + path);
		}

		Log.vvln("Opening file " + path + " success.");
	}

	public FileManager(String path)
	{
		this.path = path;
		try
		{
			open(path, false);
		} catch(IOException e){}
	}

	public FileManager(String path, boolean create) throws IOException
	{
		this.path = path;
		open(path, create);
	}

	/**
	 * Write bytes to a file.
	 * @param bytes The bytes to write.
	 * @return Whether or not the bytes were written.
	 */
	public boolean writeBytes(byte bytes[])
	{
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
		return writeBytes(s.getBytes());
	}

	/**
	 * Write a String to a file.
	 * @param s The string to write.
	 * @return Whether or not the string was written.
	 */
	public boolean println(String s)
	{
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
	 * Close the file 
	 */
	public void close()
	{
		try { fos.close(); } catch(Exception e){}
		try { fis.close(); } catch(Exception e){}

		fos = null;
		fis = null;
		file = null;
		opened = false;
		path = null;

		Log.vvln("Closed file: " + path);
	}

	/**
	 * Delete and close the file.
	 */
	public void deleteAndClose()
	{
		file.delete();
		close();
	}

	private File file;
	private FileInputStream fis;
	private FileOutputStream fos;
	private boolean opened;
	private String path;
}
