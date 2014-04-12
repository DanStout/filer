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
import java.util.Arrays;

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
			while (true)
			{
				Socket socket = serverSocket.accept();
				System.out.println("Connected to client" + socket.getInetAddress() + " on " + socket.getPort());

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
	class ClientThread implements Runnable
	{
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

				is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();

				DataInputStream dis = new DataInputStream(is);

				byte action = dis.readByte();
				System.out.println(System.currentTimeMillis() + ": Action recieved = " + action);

				// get the file from the client
				if (action == 0)
				{
					FileOutputStream fos = new FileOutputStream("files\\" + dis.readUTF());

					int bufferSize = clientSocket.getReceiveBufferSize();
					byte[] buffer = new byte[bufferSize];

					int count;
					while ((count = is.read(buffer)) > 0)
					{
						fos.write(buffer, 0, count);
					}
					System.out.println("File written");

					fos.flush();
					fos.close();

				}
				// send file to client
				else if (action == 1)
				{
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(new File("files\\").listFiles());

					System.out.println("List of files sent to client");

					System.out.println(System.currentTimeMillis() + ": reading filename");
					// make a filepath with the path retrieved from the DataInputStream
					File file = new File("files\\" + dis.readUTF());

					System.out.println("Reading " + file.getName());

					// load the above file from inside the "files" folder

					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);

					DataOutputStream dos = new DataOutputStream(os);

					int count;

					byte[] buffer = new byte[(int) file.length()];

					while ((count = bis.read(buffer)) > 0)
					{
						dos.write(buffer, 0, count);
					}

					System.out.println(Arrays.toString(buffer));
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
