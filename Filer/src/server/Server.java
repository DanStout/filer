package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    private static ArrayList<File> myFile = new ArrayList<>();
	private static ServerSocket SSocket;
	private static Socket client;

	public static void main(String args[]) throws IOException, InterruptedException
	{
		final int portnum = 5001;
		Protocol pro = new Protocol();
		try
		{
		// attempts to create server resources
				SSocket = new ServerSocket(portnum);
				System.out.println("s");
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
				System.out.println("s");
				if (input.ready())
				{
					System.out.println("s");
					// protocol object is called to read client message
					pass = pro.process(input.readLine());
					// pass is written to the client
					output.write(pass);
					// break out of loop
					if (pass.equalsIgnoreCase("disconnect"))
						break;
               
               else if(pass.equalsIgnoreCase("ready for write to server")){
                  
               }
               
					
					//CHANGES REQUIRED
					//change
               else if(pass.equalsIgnoreCase("ready for write to client")){
               //will execute the code to send file to client
                  byte[] mybytearray = new byte[(int) myFile.length()]; //NEED TO MAKE ARRAY LIST
                  //create byte array of proper size
                  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                  //populates the array using this input stream
                  bis.read(mybytearray, 0, mybytearray.length);
                  //populate the array of bytes
                  OutputStream os = SSocket.getOutputStream();
                  //send the array of bytes over the socket
                  os.write(mybytearray, 0, mybytearray.length);
               }
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

