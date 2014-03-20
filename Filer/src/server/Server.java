package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	static ServerSocket SSocket;
	static Socket client;

	public static void main(String args[]) throws IOException, InterruptedException
	{
		final int portnum = 5000;
		Protocol pro = new Protocol();
		try
		{
		// attempts to create server resources
				SSocket = new ServerSocket(portnum);
				// accepts client request and returns another socket object
				client = SSocket.accept();
				// creates read/write tools
				PrintWriter output = new PrintWriter(client.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

			// pass string will be written back to client
			String pass;
			// infinite loop which runs until "disconnect" is returned by the InputStream
			while (true)
			{
				// when there is data to be read
				if (input.ready())
				{
					// protocol object is called to read client message
					pass = pro.process(input.readLine());
					// pass is written to the client
					output.write(pass);
					// break out of loop
					if (pass.equalsIgnoreCase("diconnect"))
						break;
				}
			}
			
		}
		catch (IOException e)
		{
			System.out.println("Error creating network socket, Port number:" + portnum);
			System.exit(1);
		}
		//finally block to close server resources
		finally{
			//try block to deal with IO errors
			try {
				if(SSocket != null)
					SSocket.close();
				if(client != null)
					client.close();
			}
			//error outputs, thread pause to delay exit
			catch(IOException e){
				System.out.println("there was an error closing the system resources, hard exit in 30 seconds.");
				Thread.sleep(30000);
				System.exit(1);
			}
			finally{
			}
		}

	}
}
