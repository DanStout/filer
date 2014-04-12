package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Store files and respond to client requests
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
public class Server
{

	private int portnum = 10500;

	public static void main(String[] args)
	{
		new Server();
		System.out.println("Server initiated");
	}

	/**
	 * Initiates the server
	 */
	public Server()
	{
		ServerSocket serverSocket;
		try
		{
			serverSocket = new ServerSocket(portnum);
			while (true) // wait for clients forever
			{
				Socket socket = serverSocket.accept();
				System.out.println("Connected to client" + socket.getInetAddress() + " on " + socket.getPort());

				// create a new thread and pass it the socket that was just started, then start the thread
				new Thread(new ClientThread(socket)).start();
			}
		}
		catch (IOException e)
		{
			System.out.println("Error creating server socket");
			e.printStackTrace();
		}
	}

	/**
	 * Each client is assigned a thread
	 */
	private class ClientThread implements Runnable
	{
		// thread instance variables
		Socket clientSocket;
		OutputStream os;
		InputStream is;

		/**
		 * Constructor for the thread
		 *
		 * @param clientSocket - the socket of the client
		 */
		public ClientThread(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}

		// overridden from interface
		public void run()
		{
			try
			{
				System.out.println("Server thread running");

				// init streams
				is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();

				DataInputStream dis = new DataInputStream(is);

				// read action from client
				byte action = dis.readByte();
				System.out.println(System.currentTimeMillis() + ": Action recieved = " + action);

				// get the file from the client
				if (action == 0)
				{
					String fullName = dis.readUTF();

					// dealing with duplicates
					int dotIndex = fullName.indexOf('.');
					String fileName = fullName.substring(0, dotIndex);
					String ext = fullName.substring(dotIndex);

					int i = 1;
					while (new File("files\\" + fileName + ext).exists())
					{
						if (i == 1) fileName += "(1)";
						else
						{
							fileName = fileName.substring(0, fileName.length() - 2) + i + ")";
						}
						i++;
					}

					// create a FileOutputStream which saves a file with the name read from the client inside the "files" folder on the server
					FileOutputStream fos = new FileOutputStream("files\\" + fileName + ext);

					// reading the bytes from the client and writing them to the file
					int bufferSize = clientSocket.getReceiveBufferSize();
					byte[] buffer = new byte[bufferSize];

					int count;
					while ((count = is.read(buffer)) > 0)
					{
						fos.write(buffer, 0, count);
					}
					System.out.println("File written");

					fos.close();

				}
				// send file to client
				else if (action == 1)
				{
					// create an ObjectOutputStream
					ObjectOutputStream oos = new ObjectOutputStream(os);

					// write the array of all the files in the "files" folder to the client
					oos.writeObject(new File("files\\").listFiles());

					System.out.println("Sent list of files to client");

					System.out.println(System.currentTimeMillis() + ": reading filename");

					// make a filepath with the path retrieved from the DataInputStream
					File file = new File("files\\" + dis.readUTF());

					System.out.println("Reading " + file.getName());

					// read the file on the server and send the bytes to the client
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);

					DataOutputStream dos = new DataOutputStream(os);

					int count;

					byte[] buffer = new byte[(int) file.length()];

					while ((count = bis.read(buffer)) > 0)
					{
						dos.write(buffer, 0, count);
					}

					// close streams
					dos.close();
					bis.close();

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
