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
	// instance variables
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
		// initialize the socket
		socket = new Socket(hostname, portnum);
		// set the socket to time out after 1s
		socket.setSoTimeout(1000);

		// init output/input streams
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
		// when the server receives 0 it will do the sending file procedures
		dos.writeByte(0);
		dos.flush();

		// write the name of the passed file to the server
		dos.writeUTF(file.getName());
		dos.flush();

		// reading the file and sending it to the server as bytes from an array
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);

		int count;

		byte[] buffer = new byte[(int) file.length()];

		while ((count = bis.read(buffer)) > 0)
		{
			dos.write(buffer, 0, count);
		}

		// close the streams
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
		// tell the server we're trying to get a file
		dos.writeByte(1);

		// init the array to store the list of files stored
		File[] files = null;

		// read the files stored on server
		ois = new ObjectInputStream(socket.getInputStream());

		// cast the received object to an array of files type
		files = (File[]) ois.readObject();

		// if there were actually files on the server
		if (files != null)
		{
			// make an array filled with the filenames
			String[] filenames = new String[files.length];

			for (int i = 0; i < files.length; i++)
			{
				filenames[i] = files[i].getName();
			}

			System.out.println("File list received");

			return filenames;
		}
		else throw new Exception(); // dealt with in GUI
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

		// init stringbuilder to convert from bytes in a stream to a string
		StringBuilder sb = new StringBuilder();

		// init string to store current line
		String line;

		// read the stream and add it to the stringbuilder
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		while ((line = br.readLine()) != null)
		{
			sb.append(line);
		}

		System.out.println("File written");

		return sb.toString();
	}
}
