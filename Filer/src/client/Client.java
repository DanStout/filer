package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Communicates with the server
 * 
 * @author Nick Brooks
 * @author George Faraj
 * @author Andy Kenney
 * @author George Sousa
 * @author Daniel Stout
 * 
 * @version 2014/Apr/1
 * 
 */
public class Client
{
	Socket socket;
	InputStream is;
	BufferedOutputStream bos;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;
	ObjectInputStream ois;

	/**
	 * Initiates the client
	 *
	 * @param hostname - hostname of server (IP address)
	 * @param portnum - port of server (Should be above 1023 as below that are reserved)
	 */
	public Client(String hostname, int portnum)
	{
		try
		{
			socket = new Socket(hostname, portnum);
			bos = new BufferedOutputStream(socket.getOutputStream());
			dos = new DataOutputStream(bos);
		}
		catch (Exception e)
		{
			System.out.println("Unable to make connection");
			e.printStackTrace();
		}
	}

	/**
	 * Sends a file to the server
	 * 
	 * @param file - the file to send
	 */
	public void sendFile(File file)
	{
		try
		{
			dos.writeByte(0);
			dos.flush();

			long fileSize = file.length();

			byte[] buffer = new byte[(int) fileSize];

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			int count;

			dos.writeUTF(file.getName());
			dos.flush();

			while ((count = bis.read(buffer)) > 0)
			{
				dos.write(buffer, 0, count);
			}

			System.out.print("File sent: ");
			for (int i = 0; i < buffer.length; i++)
			{
				System.out.print((char) buffer[i]);
			}

			bos.close();
			bis.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the list of files on the server
	 * 
	 * @return an array of the names of the files on the server
	 */
	public String[] getFileList()
	{
		File[] files = null;

		try
		{
			dos.writeByte(1);
			dos.flush();

			ois = new ObjectInputStream(socket.getInputStream());

			files = (File[]) ois.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		String[] filenames = new String[files.length];

		for (int i = 0; i < files.length; i++)
		{
			filenames[i] = files[i].getName();
		}

		System.out.println("File list received");
		return filenames;
	}

	/**
	 * Gets the contents of a file from the server
	 * 
	 * @param filename - the name of the file selected
	 * @return the contents of the file as a String
	 */
	public String getFileContents(String filename)
	{
		try
		{
			dos.writeUTF(filename);
			dos.flush();
			System.out.println("getting: " + filename);

			String contents = "";

			System.out.println(ois.read());

			return contents;

		}
		catch (Exception ex)
		{

		}
		return "";
	}
}
