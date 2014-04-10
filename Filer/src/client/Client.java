package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

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
	OutputStream os;
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
	public Client(String hostname, int portnum) throws Exception
	{
		socket = new Socket(hostname, portnum);
		socket.setSoTimeout(1000);

		dos = new DataOutputStream(socket.getOutputStream());
		is = socket.getInputStream();
	}

	/**
	 * Sends a file to the server
	 * 
	 * @param file - the file to send
	 */
	public void sendFile(File file) throws Exception
	{
		dos.writeByte(0);
		dos.flush();

		dos.writeUTF(file.getName());
		dos.flush();

		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);

		int count;

		byte[] buffer = new byte[(int) file.length()];

		while ((count = bis.read(buffer)) > 0)
		{
			dos.write(buffer, 0, count);
		}
		System.out.println(Arrays.toString(buffer));
		dos.close();
		bis.close();

		System.out.println("File sent to server");

	}

	/**
	 * Gets the list of files on the server
	 * 
	 * @return an array of the names of the files on the server
	 */
	public String[] getFileList() throws Exception
	{
		File[] files = null;
		dos.writeByte(1);

		ois = new ObjectInputStream(socket.getInputStream());

		files = (File[]) ois.readObject();
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
	public String getFileContents(String filename) throws Exception
	{
		System.out.println(System.currentTimeMillis() + ": Getting " + filename);

		// send the server the name of the file you want
		dos.writeUTF(filename);

		StringBuilder sb = new StringBuilder();

		String line;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		while ((line = br.readLine()) != null)
		{
			sb.append(line);
		}

		System.out.println("File written");

		return sb.toString();
	}
}
