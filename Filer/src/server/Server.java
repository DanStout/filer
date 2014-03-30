package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
						File temp = null;
						
						//getting file name
						input.ready();
						String name = input.readLine();
						
						//creates file object and names it.
						temp = new File(name);
						
						//create input stream for file
						InputStream in = client.getInputStream();
						
						byte[] mybytearray = new byte[4096];
						//creates a byte array 4 kb in size
	   		         	FileOutputStream fos = new FileOutputStream(temp);
	   		         	//creates file output stream to create a file
	   		         	BufferedOutputStream bos = new BufferedOutputStream(fos);
	   		         	//outputs stream to the file
	   		         	int bytesRead = in.read(mybytearray, 0, mybytearray.length);
	   		         	//reads in the array of bytes to make the file
	   		         	bos.write(mybytearray, 0, bytesRead);
	   		         	//writes the array to the file
	   		         	bos.close();
	   		         	//close stream
	   		         	in.close();
	   		         	
	   		         	myFile.add(temp);
	   		         	output.write("disconnect");
                  
					}
				
					//this will trigger if the client requests a list of the files on the server
					else if (pass.equalsIgnoreCase("return file list")){
						output.write(getFileN(myFile));
					}
               
					
					//CHANGES REQUIRED
					//change
					//this if statement triggers if the client has requested the pulling of a file
					else if(pass.equalsIgnoreCase("ready for write to client")){
						int n = 0;
            	   //sends list of files to client
						output.write(getFileN(myFile));
						//waits for reply of file choice
						input.ready();
						//try to parse the line into an int
						try{
						 n = Integer.parseInt(input.readLine());
						}
						//catch
						catch(IOException e){
							System.out.println("there was an error getting the file choice from the client");
						}
						
            	   
						//will execute the code to send file to client
						byte[] mybytearray = new byte[(int) myFile.get(n).length()]; //NEED TO MAKE ARRAY LIST
						//create byte array of proper size
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile.get(n)));
						//populates the array using this input stream
						bis.read(mybytearray, 0, mybytearray.length);
						//	populate the array of bytes
						OutputStream os = client.getOutputStream();
						//send the array of bytes over the socket
						os.write(mybytearray, 0, mybytearray.length);
						//close the buffered input stream
						bis.close();
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
	//this will return a list of the file names in the array list
	public static String getFileN(ArrayList<File> myFile){
		//count used to present the files number on the server
		int count = 0;
		String ret = "[" + count +" " ;
		//loop through to find name
		for (int i = 0; i < myFile.size(); i++){
			//increment count
			count++;
			ret += myFile.get(i).getName();
			ret += ", " + count + " ";
		}
		ret += " ]";
		return ret;
	}
}

