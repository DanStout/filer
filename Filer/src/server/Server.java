package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{

	private int portnum = 10500;

	public static void main(String[] args)
	{
		new Server();
	}

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

	class ClientThread implements Runnable
	{
		Socket clientSocket;

		public ClientThread(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}

		public void run()
		{
			System.out.println("Server thread running");
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				InputStream is = clientSocket.getInputStream();
				DataInputStream dis = new DataInputStream(is);

				System.out.println("Attempting to read byte");
				byte action = dis.readByte();
				System.out.println("Action: " + action);
				
				//getting file from client
				if (action == 0)
				{
					FileOutputStream fos = new FileOutputStream("files\\" + dis.readUTF());
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					int bufferSize = clientSocket.getReceiveBufferSize();
					byte[] buffer = new byte[bufferSize];

					int count;
					while ((count = is.read(buffer)) > 0)
					{
						bos.write(buffer, 0, count);
					}
					System.out.println("File written");

					fos.flush();
					bos.flush();
					fos.close();
					bos.close();

				}
				//send file to client
				else if (action == 1)
				{

					oos.writeObject(new File("files\\").listFiles());

					System.out.println("List of files sent");

					File file = new File(dis.readUTF());

					FileInputStream fis = new FileInputStream("files\\" + file);
					BufferedInputStream bis = new BufferedInputStream(fis);

					long fileSize = file.length();

					byte[] buffer = new byte[(int) fileSize];

					int count;

					while ((count = bis.read(buffer)) > 0)
					{
						oos.write(buffer, 0, count);
					}

					oos.close();
					System.out.println("files sent");
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
